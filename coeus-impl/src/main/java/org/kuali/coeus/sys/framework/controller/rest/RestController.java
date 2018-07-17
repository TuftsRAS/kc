/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.sys.framework.rest.*;
import org.kuali.coeus.sys.framework.validation.ErrorMessage;
import org.kuali.coeus.sys.framework.validation.ErrorMessageMap;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/*
	WARNING:  Do not use @ExceptionHandler annotations here since they do not work for all of our SpringMVC Controllers.
	Exceptions must be handled by the more traditional HandlerExceptionResolver.
 */
public abstract class RestController implements HandlerExceptionResolver, Ordered {

	private static Logger LOG = LogManager.getLogger(RestController.class);

	private int order = 0;

	@Resource(name="restPropertyEditors")
	private Map<Class<?>, ? extends PropertyEditor> restPropertyEditors;

	@InitBinder
	public void initInstantBinder(WebDataBinder binder) {
		restPropertyEditors.forEach(binder::registerCustomEditor);
    }

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (ex instanceof MethodArgumentNotValidException) {
			return validationError(request, response, handler, (MethodArgumentNotValidException) ex);
		} else if(ex instanceof DataDictionaryValidationException) {
			return dataDictionaryValidationError(request, response, handler, (DataDictionaryValidationException) ex);
		} else if (ex instanceof ResourceNotFoundException) {
			return resourceNotFoundError(request, response, handler, (ResourceNotFoundException) ex);
		} else if (ex instanceof UnauthorizedAccessException) {
			return unauthorizedError(request, response, handler, (UnauthorizedAccessException) ex);
		} else if (ex instanceof UnprocessableEntityException) {
            return unprocessableEntityError(request, response, handler, (UnprocessableEntityException) ex);
        } else if (ex instanceof BadRequestException) {
            return badRequestError(request, response, handler, (UnprocessableEntityException) ex);
        } else if (ex instanceof NotImplementedException) {
            return notImplementedError(request, response, handler, (NotImplementedException) ex);
        }
        else {
			return unrecognizedException(request, response, handler, ex);
		}
	}

	protected ModelAndView validationError(HttpServletRequest request, HttpServletResponse response, Object handler, MethodArgumentNotValidException ex) {
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
		List<String> errors = new ArrayList<>(fieldErrors.size() + globalErrors.size());
		String error;
		for (FieldError fieldError : fieldErrors) {
			error = fieldError.getField() + ", " + fieldError.getDefaultMessage();
			errors.add(error);
		}
		for (ObjectError objectError : globalErrors) {
			error = objectError.getObjectName() + ", " + objectError.getDefaultMessage();
			errors.add(error);
		}

		return createJsonModelAndView(HttpStatus.BAD_REQUEST.value(), new ErrorMessage(errors), response);
	}

	protected ModelAndView createJsonModelAndView(int status, Object model, HttpServletResponse response) {
		response.setStatus(status);
		final MappingJackson2JsonView view = new MappingJackson2JsonView();
		return new ModelAndView(view, "Error", model);
	}

	protected ModelAndView dataDictionaryValidationError(HttpServletRequest request, HttpServletResponse response, Object handler, DataDictionaryValidationException ex) {
		return createJsonModelAndView(HttpStatus.UNPROCESSABLE_ENTITY.value(), new ErrorMessageMap(ex.getErrors()), response);
	}

	protected ModelAndView resourceNotFoundError(HttpServletRequest request, HttpServletResponse response, Object handler, ResourceNotFoundException ex) {
		return createJsonModelAndView(HttpStatus.NOT_FOUND.value(), generateSingleErrorFromExceptionMessage(ex), response);
	}

    protected ModelAndView unprocessableEntityError(HttpServletRequest request, HttpServletResponse response, Object handler, UnprocessableEntityException ex) {
        if (ex.getCause() != null) {
			LOG.info(ex.getMessage(), ex);
		}

		return createJsonModelAndView(HttpStatus.UNPROCESSABLE_ENTITY.value(), generateSingleErrorFromExceptionMessage(ex), response);
    }

    protected ModelAndView badRequestError(HttpServletRequest request, HttpServletResponse response, Object handler, UnprocessableEntityException ex) {
        return createJsonModelAndView(HttpStatus.BAD_REQUEST.value(), generateSingleErrorFromExceptionMessage(ex), response);
    }

	protected ModelAndView unauthorizedError(HttpServletRequest request, HttpServletResponse response, Object handler, UnauthorizedAccessException ex) {
		return createJsonModelAndView(HttpStatus.UNAUTHORIZED.value(), generateSingleErrorFromExceptionMessage(ex), response);
	}

	protected ErrorMessage generateSingleErrorFromExceptionMessage(Exception ex) {
		return new ErrorMessage(Collections.singletonList(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "Unknown Error: " + ex.getClass().getName()));
	}

	protected ModelAndView unrecognizedException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		LOG.error(ex.getMessage(), ex);
		return createJsonModelAndView(HttpStatus.INTERNAL_SERVER_ERROR.value(), generateSingleErrorFromExceptionMessage(ex), response);
	}

    protected ModelAndView notImplementedError(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LOG.error(ex.getMessage(), ex);
        return createJsonModelAndView(HttpStatus.NOT_IMPLEMENTED.value(), generateSingleErrorFromExceptionMessage(ex), response);
    }

	@Override
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Map<Class<?>, ? extends PropertyEditor> getRestPropertyEditors() {
		return restPropertyEditors;
	}

	public void setRestPropertyEditors(Map<Class<?>, ? extends PropertyEditor> restPropertyEditors) {
		this.restPropertyEditors = restPropertyEditors;
	}

}