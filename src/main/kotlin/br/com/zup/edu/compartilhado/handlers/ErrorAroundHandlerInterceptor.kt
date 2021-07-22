package br.com.zup.edu.compartilhado.handlers

import br.com.zup.edu.exception.ChavePixExistenteException
import br.com.zup.edu.exception.RecursoNaoEcontradoException
import br.com.zup.edu.exception.RecursoNaoPermitidoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorAroundHandler::class)
class ErrorAroundHandlerInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            val responseObserver = context.parameterValues[1] as StreamObserver<*>

            val status = when (ex) {
                is ChavePixExistenteException -> Status.ALREADY_EXISTS
                    .withDescription(ex.message)

                is RecursoNaoEcontradoException -> Status.NOT_FOUND
                    .withDescription(ex.message)

                is ConstraintViolationException -> Status.INVALID_ARGUMENT
                    .withDescription(ex.message)

                is RecursoNaoPermitidoException -> Status.PERMISSION_DENIED
                    .withDescription(ex.message)

                is IllegalStateException -> Status.FAILED_PRECONDITION
                    .withDescription(ex.message)

                else -> Status.UNKNOWN
                    .withCause(ex)
                    .withDescription(ex.message)
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null

    }
}