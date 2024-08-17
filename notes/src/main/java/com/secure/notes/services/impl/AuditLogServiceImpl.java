package com.secure.notes.services.impl;

import com.secure.notes.models.AuditLog;
import com.secure.notes.models.Note;
import com.secure.notes.repositories.AuditLogRepository;
import com.secure.notes.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    AuditLogRepository auditLogRepository ;

    @Override
    public void logNoteCreation(String userName, Note note){
        AuditLog auditLog=new AuditLog() ;
        auditLog.setAction("CREATION");
        auditLog.setUsername(userName);
        auditLog.setNoteContent(note.getContent());
        auditLog.setNoteId(note.getId());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog) ;

    }
    @Override
    public void logNoteUpdate(String userName, Note note){
        AuditLog auditLog=new AuditLog() ;
        auditLog.setAction("UPDATE");
        auditLog.setUsername(userName);
        auditLog.setNoteContent(note.getContent());
        auditLog.setNoteId(note.getId());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog) ;

    }

    @Override
    public void logNoteDeletion(String userName, Long noteId){
        AuditLog auditLog=new AuditLog() ;
        auditLog.setAction("DELETE");
        auditLog.setUsername(userName);

        auditLog.setNoteId(noteId);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog) ;

    }

    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll() ;
    }

    @Override
    public List<AuditLog> getAuditLogsForNoteId(Long id) {
        return auditLogRepository.findByNoteId(id);
    }
}
