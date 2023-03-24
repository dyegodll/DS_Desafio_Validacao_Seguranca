package com.devsuperior.bds04.controllers.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //identifica classe responsável por tratar exceptions dos controladores
public class ResourcesExceptionsHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class) //identifica método que intercepta a exception desse tipo com a exceção correspondente
	public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request){
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; //tipo ENUN de HTTP (erro de processamento de validação de entidade = 422)
		ValidationError err = new ValidationError();
		err.setTimestamp(Instant.now());
		err.setStatus(status.value()); 
		err.setError("Validation Exception");
		err.setMessage(e.getMessage());
		err.setPath(request.getRequestURI());
		
		//erros de validação dentro da MethodArgumentNotValidException
		// e.getBindingResult() //acessa os resultados vinculados a Exception
			// .getFieldErrors(); //acessa os FildErros dentro dos resultados e converte em lista, para poder capturar o campo e a msg
		
		//para cada FieldError dentro da Exception, adicione na lista List<FieldMessage> de ValidationError
		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			err.addError(f.getField(), f.getDefaultMessage());
		}
		
		//status define o status da requisição
		return ResponseEntity.status(status).body(err);
	}
}
