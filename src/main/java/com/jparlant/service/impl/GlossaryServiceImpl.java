package com.jparlant.service.impl;

import com.jparlant.entity.Glossary;
import com.jparlant.mapper.GlossaryMapper;
import com.jparlant.service.GlossaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlossaryServiceImpl implements GlossaryService {

    @Autowired
    private GlossaryMapper glossaryMapper;

    @Override
    public List<Glossary> list() {
        return glossaryMapper.selectAll();
    }

    @Override
    public Glossary getById(Long id) {
        return glossaryMapper.selectById(id);
    }

    @Override
    public List<Glossary> listByAgentId(Long agentId) {
        return glossaryMapper.selectByAgentId(agentId);
    }

    @Override
    public Glossary create(Glossary glossary) {
        glossaryMapper.insert(glossary);
        return glossary;
    }

    @Override
    public Glossary update(Long id, Glossary glossary) {
        glossary.setId(id);
        glossaryMapper.updateById(glossary);
        return glossaryMapper.selectById(id);
    }

    @Override
    public boolean delete(Long id) {
        return glossaryMapper.deleteById(id) > 0;
    }
}
