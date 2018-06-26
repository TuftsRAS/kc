/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.award.finance;

import com.codiform.moo.curry.Translate;

import org.kuali.coeus.award.api.AwardApiService;
import org.kuali.coeus.award.dto.AwardDto;
import org.kuali.coeus.award.finance.dao.AccountDao;
import org.kuali.coeus.common.api.document.service.CommonApiService;
import org.kuali.coeus.sys.framework.controller.rest.RestController;
import org.kuali.coeus.sys.framework.gv.GlobalVariableService;
import org.kuali.coeus.sys.framework.rest.ResourceNotFoundException;
import org.kuali.coeus.sys.framework.rest.SearchResults;
import org.kuali.coeus.sys.framework.rest.UnauthorizedAccessException;
import org.kuali.kra.award.dao.AwardDao;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.FeatureFlagConstants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.kim.bo.KcKimAttributes;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequestMapping(value="/api")
@Controller("awardAccountController")
public class AwardAccountController extends RestController {

    public static final String NO_SUCH_ACCOUNT = "No such account.";
    @Autowired
    @Qualifier("accountDao")
    private AccountDao accountDao;

    @Autowired
    @Qualifier("dataObjectService")
    private DataObjectService dataObjectService;

    @Autowired
    @Qualifier("awardDao")
    private AwardDao awardDao;

    @Autowired
    @Qualifier("commonApiService")
    private CommonApiService commonApiService;

    @Autowired
    @Qualifier("awardApiService")
    private AwardApiService awardApiService;

    @Autowired
    @Qualifier("permissionService")
    private PermissionService permissionService;

    @Autowired
    @Qualifier("globalVariableService")
    private GlobalVariableService globalVariableService;

    @Autowired
    @Qualifier("parameterService")
    private ParameterService parameterService;

    @RequestMapping(value="/v1/accounts")
    public @ResponseBody
    AccountResults getAccounts(@RequestParam(value = "startIndex", required = false) Integer startIndex,
                                     @RequestParam(value = "size", required = false) Integer size) {
        assertUserHasAccountReadAccess();
        return Translate.to(AccountResults.class).from(transformSearchResults(getAccountDao().getAccounts(startIndex, size)));
    }

    @RequestMapping(method=RequestMethod.PUT, value="v1/accounts/{accountNumber}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    public
    void updateAccount(@Valid @RequestBody AccountDto account, @PathVariable String accountNumber) {
        assertUserHasAccountWriteAccess();
        AwardAccount currentAccount = accountDao.getAccount(accountNumber);
        if(Objects.isNull(currentAccount)) {
            sendErrorResponse(NO_SUCH_ACCOUNT);
        } else {
            if (account.getStatus() != null) currentAccount.setStatus(account.getStatus());
            if (account.getPending() != null) currentAccount.setPending(account.getPending());
            if (account.getBudgeted() != null) currentAccount.setBudgeted(account.getBudgeted());
            if (account.getAvailable() != null) currentAccount.setAvailable(account.getAvailable());
            if (account.getExpense() != null) currentAccount.setExpense(account.getExpense());
            if (account.getIncome() != null) currentAccount.setIncome(account.getIncome());
            if (account.getComment() != null) currentAccount.setComment(account.getComment());

            accountDao.saveAccount(currentAccount);
        }
    }

    @RequestMapping(method=RequestMethod.GET, value="v1/accounts/{accountNumber}")
    public @ResponseBody
    AccountResults getAccount(@PathVariable String accountNumber) {
        assertUserHasAccountReadAccess();
        AwardAccount account = accountDao.getAccount(accountNumber);
        ArrayList<AwardAccount> accounts = new ArrayList<>();
        accounts.add(account);
        if(Objects.isNull(account)) {
            sendErrorResponse(NO_SUCH_ACCOUNT);
        }
        return Translate.to(AccountResults.class).from(transformSearchResults(accounts));
    }

    @RequestMapping(method= RequestMethod.GET, value="v1/award-posts",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<AwardPostsDto> getAwardPosts(@RequestParam(value="accountNumber", required=false) String accountNumber) {
        assertUserHasPostReadAccess();
        List<AwardPosts> awardPostsList = getAccountDao().getActiveAwardPosts(accountNumber);
        return awardPostsList.stream()
                .map(this::translateAwardPosts)
                .collect(Collectors.toList());
    }

    protected AwardPostsDto translateAwardPosts(AwardPosts awardPosts) {
        AwardPostsDto awardPostsDto = commonApiService.convertObject(awardPosts, AwardPostsDto.class);
        Award award = awardDao.getAward(awardPosts.getAwardId());
        AwardDto awardDto = awardApiService.convertAwardToDto(award);
        awardPostsDto.setAwardDto(awardDto);
        return awardPostsDto;
    }

    @RequestMapping(method= RequestMethod.PUT, value="v1/award-posts/{postId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public AwardPostsDto getAwardPosts(@PathVariable Long postId, @RequestBody AwardPostsDto awardPostsDto) {
        assertUserHasPostWriteAccess();
        AwardPosts awardPost = getAccountDao().getAwardPostsById(postId);
        if(awardPost == null) {
            throw new ResourceNotFoundException("The award post entry with id " + postId + " was not found.");
        }
        awardPost.setActive(awardPostsDto.isActive());
        dataObjectService.save(awardPost);
        return commonApiService.convertObject(awardPost, AwardPostsDto.class);
    }

    SearchResults<AwardAccount> transformSearchResults(List<AwardAccount> accounts) {
        SearchResults<AwardAccount> result = new SearchResults<>();
        result.setResults(accounts);
        result.setTotalResults(accounts.size());
        return result;
    }

    public boolean isApiAuthEnabled() {
        return parameterService.getParameterValueAsBoolean(Constants.MODULE_NAMESPACE_GEN, ParameterConstants.DOCUMENT_COMPONENT,
                FeatureFlagConstants.ENABLE_API_AUTHORIZATION);
    }

    protected void assertUserHasPostReadAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null ||
                    !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                            Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.READ_CLASS,
                            Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardPosts.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasPostWriteAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null ||
                    !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                            Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.WRITE_CLASS,
                            Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardPosts.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasAccountReadAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null ||
                    !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                            Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.READ_CLASS,
                            Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardAccount.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void assertUserHasAccountWriteAccess() {
        if (isApiAuthEnabled()) {
            if (globalVariableService.getUserSession() == null ||
                    !permissionService.hasPermissionByTemplate(globalVariableService.getUserSession().getPrincipalId(),
                            Constants.MODULE_NAMESPACE_SYSTEM, PermissionConstants.WRITE_CLASS,
                            Collections.singletonMap(KcKimAttributes.CLASS_NAME, AwardAccount.class.getName()))) {
                throw new UnauthorizedAccessException();
            }
        }
    }

    protected void sendErrorResponse(String msg) {
        throw new ResourceNotFoundException(msg);
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public AwardDao getAwardDao() {
        return awardDao;
    }

    public void setAwardDao(AwardDao awardDao) {
        this.awardDao = awardDao;
    }

    public CommonApiService getCommonApiService() {
        return commonApiService;
    }

    public void setCommonApiService(CommonApiService commonApiService) {
        this.commonApiService = commonApiService;
    }

    public AwardApiService getAwardApiService() {
        return awardApiService;
    }

    public void setAwardApiService(AwardApiService awardApiService) {
        this.awardApiService = awardApiService;
    }

    public PermissionService getPermissionService() {
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public GlobalVariableService getGlobalVariableService() {
        return globalVariableService;
    }

    public void setGlobalVariableService(GlobalVariableService globalVariableService) {
        this.globalVariableService = globalVariableService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
