package com.jparlant.service;

import com.jparlant.entity.Glossary;
import java.util.List;

public interface GlossaryService {

    List<Glossary> list();

    Glossary getById(Long id);

    List<Glossary> listByAgentId(Long agentId);

    Glossary create(Glossary glossary);

    Glossary update(Long id, Glossary glossary);

    boolean delete(Long id);
}
