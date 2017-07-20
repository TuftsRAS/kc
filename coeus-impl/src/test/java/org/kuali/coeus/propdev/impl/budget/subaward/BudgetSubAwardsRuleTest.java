package org.kuali.coeus.propdev.impl.budget.subaward;


import junit.framework.Assert;
import org.junit.Test;
import org.kuali.coeus.common.framework.attachment.KcAttachmentService;
import org.kuali.coeus.common.framework.ruleengine.KcEventResult;
import org.kuali.coeus.common.impl.attachment.KcAttachmentServiceImpl;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class BudgetSubAwardsRuleTest {

  @Test
  public void noAttachment() {
    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();
    BudgetSubAwards subAwards = new BudgetSubAwards();

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertTrue(result.getSuccess());
  }

  @Test
  public void attachmentNotPdf() throws IOException {
    DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
    Resource resource = loader.getResource("classpath:attachments/plain.txt");
    Assert.assertNotNull(resource);
    MultipartFile notPdfFile = new MockMultipartFile(resource.getFilename(), resource.getFilename(), "text/plain", resource.getInputStream());

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();
    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(notPdfFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertFalse(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasErrors());
    Assert.assertEquals(result.getMessageMap().getErrorMessages().get("newSubAwardFile").get(0).getErrorKey(), Constants.SUBAWARD_FILE_REQUIRED);
  }

  @Test
  public void emptyAttachment() throws IOException {
    MultipartFile emptyFile = new MockMultipartFile("empty.pdf", "empty.pdf", "application/pdf", new byte[0]);

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();
    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(emptyFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertFalse(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasErrors());
    Assert.assertEquals(result.getMessageMap().getErrorMessages().get("newSubAwardFile").get(0).getErrorKey(), Constants.SUBAWARD_FILE_REQUIRED);
  }

  @Test
  public void invalidPdfAttachment() throws IOException {
    DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
    Resource resource = loader.getResource("classpath:attachments/invalid.pdf");
    Assert.assertNotNull(resource);
    MultipartFile invalidFile = new MockMultipartFile(resource.getFilename(), resource.getFilename(), "application/pdf", resource.getInputStream());

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();
    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(invalidFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertFalse(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasErrors());
    Assert.assertEquals(result.getMessageMap().getErrorMessages().get("newSubAwardFile").get(0).getErrorKey(), Constants.SUBAWARD_FILE_REQUIRED);
  }

  @Test
  public void validPdfAttachment() throws IOException {
    DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
    Resource resource = loader.getResource("classpath:attachments/valid.pdf");
    Assert.assertNotNull(resource);
    MultipartFile invalidFile = new MockMultipartFile(resource.getFilename(), resource.getFilename(), "application/pdf", resource.getInputStream());

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();

    KcAttachmentService kcAttachmentService = new KcAttachmentServiceImpl();
    rule.setKcAttachmentService(kcAttachmentService);

    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(invalidFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertTrue(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasNoErrors());
  }

  @Test
  public void encryptedPdf() throws IOException {
    DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
    Resource resource = loader.getResource("classpath:attachments/valid_encrypted.pdf");
    Assert.assertNotNull(resource);
    MultipartFile invalidFile = new MockMultipartFile(resource.getFilename(), resource.getFilename(), "application/pdf", resource.getInputStream());

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();
    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(invalidFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertFalse(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasErrors());
    Assert.assertEquals(result.getMessageMap().getErrorMessages().get("newSubAwardFile").get(0).getErrorKey(), Constants.SUBAWARD_FILE_ENCRYPTED);
  }

  @Test
  public void encryptedAttachmentPdf() throws IOException {
    DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
    Resource resource = loader.getResource("classpath:attachments/encrypted_attachment.pdf");
    Assert.assertNotNull(resource);
    MultipartFile invalidFile = new MockMultipartFile(resource.getFilename(), resource.getFilename(), "application/pdf", resource.getInputStream());

    BudgetSubAwardsRule rule = new BudgetSubAwardsRule();

    KcAttachmentService kcAttachmentService = new KcAttachmentServiceImpl();
    rule.setKcAttachmentService(kcAttachmentService);

    BudgetSubAwards subAwards = new BudgetSubAwards();
    subAwards.setNewSubAwardFile(invalidFile);

    BudgetSubAwardsEvent event = new BudgetSubAwardsEvent(subAwards, null, "foo");

    KcEventResult result = new KcEventResult();
    rule.verifyAttachment(event, result);
    Assert.assertFalse(result.getSuccess());
    Assert.assertTrue(result.getMessageMap().hasErrors());
    Assert.assertEquals(result.getMessageMap().getErrorMessages().get("newSubAwardFile").get(0).getErrorKey(), Constants.SUBAWARD_FILE_ENCRYPTED);
  }
}