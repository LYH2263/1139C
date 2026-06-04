package com.wordmind.service;

import com.wordmind.dto.WordDTO;
import com.wordmind.entity.Word;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WordService {
    
    @Autowired
    private WordRepository wordRepository;
    
    public WordDTO.ListResponse getWords(String keyword, String pos, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Word> wordPage = wordRepository.searchWords(keyword, pos, pageable);
        
        List<WordDTO.Response> list = wordPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return WordDTO.ListResponse.builder()
                .list(list)
                .total(wordPage.getTotalElements())
                .page(page)
                .size(size)
                .build();
    }
    
    public WordDTO.Response getWordById(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("单词不存在"));
        return convertToDTO(word);
    }
    
    @Transactional
    public WordDTO.Response createWord(WordDTO.CreateRequest request) {
        Word word = new Word();
        word.setWord(request.getWord());
        word.setPhonetic(request.getPhonetic());
        word.setPos(request.getPos());
        word.setMeaning(request.getMeaning());
        word.setExample(request.getExample());
        word.setMemoryTip(request.getMemoryTip());
        
        Word saved = wordRepository.save(word);
        return convertToDTO(saved);
    }
    
    @Transactional
    public WordDTO.Response updateWord(Long id, WordDTO.UpdateRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("单词不存在"));
        
        if (request.getWord() != null) word.setWord(request.getWord());
        if (request.getPhonetic() != null) word.setPhonetic(request.getPhonetic());
        if (request.getPos() != null) word.setPos(request.getPos());
        if (request.getMeaning() != null) word.setMeaning(request.getMeaning());
        if (request.getExample() != null) word.setExample(request.getExample());
        if (request.getMemoryTip() != null) word.setMemoryTip(request.getMemoryTip());
        
        Word saved = wordRepository.save(word);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void deleteWord(Long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("单词不存在"));
        wordRepository.delete(word);
    }
    
    private WordDTO.Response convertToDTO(Word word) {
        return WordDTO.Response.builder()
                .id(word.getId())
                .word(word.getWord())
                .phonetic(word.getPhonetic())
                .pos(word.getPos())
                .meaning(word.getMeaning())
                .example(word.getExample())
                .memoryTip(word.getMemoryTip())
                .createdAt(word.getCreatedAt())
                .build();
    }
}
