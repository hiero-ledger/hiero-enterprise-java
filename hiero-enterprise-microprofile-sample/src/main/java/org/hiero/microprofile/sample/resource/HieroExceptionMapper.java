package org.hiero.microprofile.sample.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import org.hiero.base.HieroException;

/**
 * Exception mapper for HieroException in the MicroProfile sample. ensures consistent JSON error
 * responses across frameworks.
 */
@Provider
public class HieroExceptionMapper implements ExceptionMapper<HieroException> {

  @Override
  public Response toResponse(final HieroException exception) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(Map.of("error", "Hiero Network Error", "message", exception.getMessage()))
        .build();
  }
}
