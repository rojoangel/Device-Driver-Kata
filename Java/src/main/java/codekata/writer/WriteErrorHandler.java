package codekata.writer;

import codekata.exception.WriteError;

public interface WriteErrorHandler {
    void handle() throws WriteError;
}
