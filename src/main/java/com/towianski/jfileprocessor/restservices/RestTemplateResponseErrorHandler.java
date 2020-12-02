/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.towianski.jfileprocessor.restservices;

import com.towianski.utils.FileUtils;
import com.towianski.utils.MyLogger;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

/**
 *
 * @author stan
 */
@Component
public class RestTemplateResponseErrorHandler 
  implements ResponseErrorHandler {
 
    private static final MyLogger logger = MyLogger.getLogger( FileUtils.class.getName() );
    
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) 
      throws IOException {
 
        return (
          httpResponse.getStatusCode().series() == CLIENT_ERROR 
          || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }
 
    @Override
    public void handleError(ClientHttpResponse httpResponse) 
      throws IOException {
 
        if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.SERVER_ERROR) {
            logger.severe( "SERVER_ERROR" );
            // handle SERVER_ERROR
        } else if (httpResponse.getStatusCode()
          .series() == HttpStatus.Series.CLIENT_ERROR) {
            logger.severe( "CLIENT_ERROR" );
            // handle CLIENT_ERROR
//            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
//                throw new NotFoundException();
//            }
//            if (httpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                throw new UnauthorizedException( "UNAUTHORIZED" );
//            }
        }
    }
}