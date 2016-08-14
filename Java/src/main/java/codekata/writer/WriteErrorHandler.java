package codekata.writer;

import codekata.exception.WriteError;

public interface WriteErrorHandler {

    byte handles();

    void handle() throws WriteError;
}
