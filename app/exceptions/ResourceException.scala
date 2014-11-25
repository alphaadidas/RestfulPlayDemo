package exceptions

/**
 * Base Exception for Rest Resources.
 *
 * @author gmatsu
 *
 *
 */
class ResourceException extends Exception {
  val statusCode = 400
}

class ResourceUnauthorizedException extends  ResourceException{
  override val statusCode = 401
}

class ResourceNotFoundException extends  ResourceException{
  override val statusCode = 404
}

class ResourceConflictException extends  ResourceException{
  override val statusCode = 409
}

class ResourcePreconditionException extends  ResourceException{
  override val statusCode = 412
}

