package com.wordmind.service;

import com.wordmind.dto.QuizDTO;
import com.wordmind.dto.WordDTO;
import com.wordmind.entity.QuizRecord;
import com.wordmind.entity.Word;
import com.wordmind.repository.QuizRecordRepository;
import com.wordmind.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {
    
    @Autowired
    private WordRepository wordRepository;
    
    @Autowired
    private QuizRecordRepository quizRecordRepository;
    
    private final Map<String, QuizSession> quizSessions = new HashMap<>();
    
    public QuizDTO.StartResponse startQuiz(Long userId, int count) {
        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);
        
        List<Word> selectedWords = allWords.stream()
                .limit(Math.min(count, allWords.size()))
                .collect(Collectors.toList());
        
        String quizId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        
        List<QuizDTO.Question> questions = selectedWords.stream()
                .map(word -> generateQuestion(word, allWords))
                .collect(Collectors.toList());
        
        quizSessions.put(quizId, new QuizSession(userId, startTime, selectedWords));
        
        return QuizDTO.StartResponse.builder()
                .quizId(quizId)
                .questions(questions)
                .build();
    }
    
    @Transactional
    public QuizDTO.SubmitResponse submitQuiz(Long userId, QuizDTO.SubmitRequest request) {
        QuizSession session = quizSessions.get(request.getQuizId());
        if (session == null) {
            throw new RuntimeException("测验不存在或已过期");
        }
        
        List<Word> words = session.getWords();
        int correctCount = 0;
        List<Word> wrongWords = new ArrayList<>();
        
        for (int i = 0; i < words.size() && i < request.getAnswers().size(); i++) {
            Word word = words.get(i);
            QuizDTO.Answer answer = request.getAnswers().get(i);
            
            if (isCorrect(word, answer)) {
                correctCount++;
            } else {
                wrongWords.add(word);
            }
        }
        
        int totalCount = words.size();
        int score = totalCount > 0 ? (correctCount * 100 / totalCount) : 0;
        int duration = (int) java.time.Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds();
        
        // 保存测验记录
        QuizRecord record = new QuizRecord();
        record.setUserId(userId);
        record.setScore(score);
        record.setCorrectCount(correctCount);
        record.setTotalCount(totalCount);
        record.setDuration(duration);
        record.setWrongWordIds(wrongWords.stream()
                .map(Word::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        quizRecordRepository.save(record);
        
        // 清理会话
        quizSessions.remove(request.getQuizId());
        
        return QuizDTO.SubmitResponse.builder()
                .score(score)
                .correctCount(correctCount)
                .totalCount(totalCount)
                .duration(duration)
                .wrongWords(wrongWords.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private QuizDTO.Question generateQuestion(Word word, List<Word> allWords) {
        // 生成选择题：选择正确释义
        List<String> options = new ArrayList<>();
        options.add(word.getMeaning());
        
        // 随机选择3个错误选项
        List<Word> otherWords = allWords.stream()
                .filter(w -> !w.getId().equals(word.getId()))
                .collect(Collectors.toList());
        Collections.shuffle(otherWords);
        
        for (int i = 0; i < Math.min(3, otherWords.size()); i++) {
            options.add(otherWords.get(i).getMeaning());
        }
        
        Collections.shuffle(options);
        
        return QuizDTO.Question.builder()
                .wordId(word.getId())
                .word(word.getWord())
                .type("CHOICE")
                .question("请选择 \"" + word.getWord() + "\" 的正确释义")
                .options(options)
                .correctAnswer(word.getMeaning())
                .build();
    }
    
    private boolean isCorrect(Word word, QuizDTO.Answer answer) {
        return word.getMeaning().equals(answer.getAnswer());
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
                .build();
    }
    
    private static class QuizSession {
        private final Long userId;
        private final LocalDateTime startTime;
        private final List<Word> words;
        
        public QuizSession(Long userId, LocalDateTime startTime, List<Word> words) {
            this.userId = userId;
            this.startTime = startTime;
            this.words = words;
        }
        
        public Long getUserId() { return userId; }
        public LocalDateTime getStartTime() { return startTime; }
        public List<Word> getWords() { return words; }
    }
}
