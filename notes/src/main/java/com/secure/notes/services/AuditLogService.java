package com.secure.notes.services;

import com.secure.notes.models.AuditLog;
import com.secure.notes.models.Note;

import java.util.List;

public interface AuditLogService {
    void logNoteCreation(String userName, Note note);
    void logNoteUpdate(String userName, Note note);
    void logNoteDeletion(String userName, Long noteId);
    List<AuditLog> getAllAuditLogs();

    List<AuditLog> getAuditLogsForNoteId(Long id);
}
