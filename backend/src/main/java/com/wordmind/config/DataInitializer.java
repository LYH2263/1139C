package com.wordmind.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wordmind.entity.*;
import com.wordmind.repository.*;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepo, WordRepository wordRepo, 
                               WordRelationRepository relationRepo, 
                               ReviewRecordRepository reviewRepo,
                               QuizRecordRepository quizRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // 创建测试用户
            if (!userRepo.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                userRepo.save(admin);
            }
            
            if (!userRepo.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(encoder.encode("user123"));
                user.setRole(User.Role.USER);
                userRepo.save(user);
            }
            
            // 创建30个不同场景的测试单词数据
            if (wordRepo.count() == 0) {
                // 场景1: 情感类 (Emotions)
                Word happy = createWord(wordRepo, "happy", "/ˈhæpi/", "adjective", 
                    "快乐的，幸福的", "I am very happy today.", "hap-py 开心的");
                Word joy = createWord(wordRepo, "joy", "/dʒɔɪ/", "noun", 
                    "欢乐，喜悦", "The children shouted with joy.", "joy 快乐");
                Word sad = createWord(wordRepo, "sad", "/sæd/", "adjective", 
                    "悲伤的", "She felt sad about the news.", "sad 悲伤");
                Word excited = createWord(wordRepo, "excited", "/ɪkˈsaɪtɪd/", "adjective",
                    "兴奋的", "I'm excited about the trip.", "ex-ci-ted 兴奋的");
                Word angry = createWord(wordRepo, "angry", "/ˈæŋɡri/", "adjective",
                    "生气的", "Don't be angry with me.", "an-gry 生气的");
                
                // 场景2: 食物类 (Food)
                Word apple = createWord(wordRepo, "apple", "/ˈæpl/", "noun", 
                    "苹果", "I eat an apple every day.", "a-pple 一个苹果");
                Word fruit = createWord(wordRepo, "fruit", "/fruːt/", "noun", 
                    "水果", "Apples are my favorite fruit.", "fruit 水果");
                Word bread = createWord(wordRepo, "bread", "/bred/", "noun",
                    "面包", "I had bread for breakfast.", "bread 面包");
                Word water = createWord(wordRepo, "water", "/ˈwɔːtər/", "noun",
                    "水", "Please drink more water.", "wa-ter 水");
                Word coffee = createWord(wordRepo, "coffee", "/ˈkɒfi/", "noun",
                    "咖啡", "I need a cup of coffee.", "cof-fee 咖啡");
                
                // 场景3: 动作类 (Actions)
                Word run = createWord(wordRepo, "run", "/rʌn/", "verb", 
                    "跑，奔跑", "I run every morning.", "run 跑");
                Word fast = createWord(wordRepo, "fast", "/fɑːst/", "adjective", 
                    "快的", "He is a fast runner.", "fast 快");
                Word walk = createWord(wordRepo, "walk", "/wɔːk/", "verb",
                    "走，步行", "Let's walk to school.", "walk 走");
                Word eat = createWord(wordRepo, "eat", "/iːt/", "verb",
                    "吃", "I eat breakfast at 7am.", "eat 吃");
                Word drink = createWord(wordRepo, "drink", "/drɪŋk/", "verb",
                    "喝", "I drink milk every day.", "drink 喝");
                
                // 场景4: 外貌类 (Appearance)
                Word beautiful = createWord(wordRepo, "beautiful", "/ˈbjuːtɪfl/", "adjective", 
                    "美丽的", "She is a beautiful girl.", "beau-ti-ful 美丽的");
                Word pretty = createWord(wordRepo, "pretty", "/ˈprɪti/", "adjective", 
                    "漂亮的", "You look pretty today.", "pret-ty 漂亮的");
                Word tall = createWord(wordRepo, "tall", "/tɔːl/", "adjective",
                    "高的", "He is very tall.", "tall 高的");
                Word short_word = createWord(wordRepo, "short", "/ʃɔːt/", "adjective",
                    "矮的，短的", "The pencil is short.", "short 短的");
                
                // 场景5: 大小类 (Size)
                Word big = createWord(wordRepo, "big", "/bɪɡ/", "adjective", 
                    "大的", "This is a big house.", "big 大");
                Word small = createWord(wordRepo, "small", "/smɔːl/", "adjective", 
                    "小的", "It's a small world.", "small 小");
                Word large = createWord(wordRepo, "large", "/lɑːdʒ/", "adjective",
                    "大的", "This is a large room.", "large 大的");
                Word tiny = createWord(wordRepo, "tiny", "/ˈtaɪni/", "adjective",
                    "微小的", "A tiny ant.", "ti-ny 微小的");
                
                // 场景6: 学习类 (Study)
                Word book = createWord(wordRepo, "book", "/bʊk/", "noun", 
                    "书", "I like reading books.", "book 书");
                Word read = createWord(wordRepo, "read", "/riːd/", "verb", 
                    "阅读", "I read books every night.", "read 读");
                Word write = createWord(wordRepo, "write", "/raɪt/", "verb",
                    "写", "Please write your name.", "write 写");
                Word study = createWord(wordRepo, "study", "/ˈstʌdi/", "verb",
                    "学习", "I study English every day.", "stu-dy 学习");
                
                // 场景7: 品质类 (Quality)
                Word good = createWord(wordRepo, "good", "/ɡʊd/", "adjective", 
                    "好的", "This is a good idea.", "good 好");
                Word bad = createWord(wordRepo, "bad", "/bæd/", "adjective", 
                    "坏的", "That's a bad habit.", "bad 坏");
                Word excellent = createWord(wordRepo, "excellent", "/ˈeksələnt/", "adjective",
                    "优秀的", "You did an excellent job!", "ex-cel-lent 优秀的");
                Word terrible = createWord(wordRepo, "terrible", "/ˈterəbl/", "adjective",
                    "糟糕的", "The weather is terrible.", "ter-ri-ble 糟糕的");
                
                // 场景8: 时间类 (Time)
                Word morning = createWord(wordRepo, "morning", "/ˈmɔːnɪŋ/", "noun",
                    "早晨", "Good morning!", "mor-ning 早晨");
                Word night = createWord(wordRepo, "night", "/naɪt/", "noun",
                    "夜晚", "Good night!", "night 夜晚");
                
                // 创建丰富的关系网络
                // 情感类关系
                createRelation(relationRepo, happy.getId(), joy.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, happy.getId(), sad.getId(), WordRelation.RelationType.ANTONYM);
                createRelation(relationRepo, excited.getId(), happy.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, angry.getId(), sad.getId(), WordRelation.RelationType.TOPIC);
                
                // 食物类关系
                createRelation(relationRepo, apple.getId(), fruit.getId(), WordRelation.RelationType.TOPIC);
                createRelation(relationRepo, bread.getId(), eat.getId(), WordRelation.RelationType.SCENE);
                createRelation(relationRepo, water.getId(), drink.getId(), WordRelation.RelationType.SCENE);
                createRelation(relationRepo, coffee.getId(), drink.getId(), WordRelation.RelationType.SCENE);
                
                // 动作类关系
                createRelation(relationRepo, run.getId(), fast.getId(), WordRelation.RelationType.TOPIC);
                createRelation(relationRepo, walk.getId(), run.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, eat.getId(), drink.getId(), WordRelation.RelationType.TOPIC);
                
                // 外貌类关系
                createRelation(relationRepo, beautiful.getId(), pretty.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, tall.getId(), short_word.getId(), WordRelation.RelationType.ANTONYM);
                
                // 大小类关系
                createRelation(relationRepo, big.getId(), small.getId(), WordRelation.RelationType.ANTONYM);
                createRelation(relationRepo, large.getId(), big.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, tiny.getId(), small.getId(), WordRelation.RelationType.SYNONYM);
                
                // 学习类关系
                createRelation(relationRepo, book.getId(), read.getId(), WordRelation.RelationType.SCENE);
                createRelation(relationRepo, write.getId(), read.getId(), WordRelation.RelationType.TOPIC);
                createRelation(relationRepo, study.getId(), read.getId(), WordRelation.RelationType.TOPIC);
                
                // 品质类关系
                createRelation(relationRepo, good.getId(), bad.getId(), WordRelation.RelationType.ANTONYM);
                createRelation(relationRepo, excellent.getId(), good.getId(), WordRelation.RelationType.SYNONYM);
                createRelation(relationRepo, terrible.getId(), bad.getId(), WordRelation.RelationType.SYNONYM);
                
                // 时间类关系
                createRelation(relationRepo, morning.getId(), night.getId(), WordRelation.RelationType.ANTONYM);
            }
            
            // 为用户 user (ID=2) 创建复习记录和测验记录（独立于单词初始化）
            User user = userRepo.findByUsername("user").orElse(null);
            if (user != null && reviewRepo.count() == 0) {
                createSampleReviewRecords(reviewRepo, user.getId());
                createSampleQuizRecords(quizRepo, user.getId());
            }
        };
    }
    
    private Word createWord(WordRepository repo, String word, String phonetic, String pos, 
                           String meaning, String example, String memoryTip) {
        Word w = new Word();
        w.setWord(word);
        w.setPhonetic(phonetic);
        w.setPos(pos);
        w.setMeaning(meaning);
        w.setExample(example);
        w.setMemoryTip(memoryTip);
        return repo.save(w);
    }
    
    private void createRelation(WordRelationRepository repo, Long source, Long target, WordRelation.RelationType type) {
        WordRelation r = new WordRelation();
        r.setSourceWordId(source);
        r.setTargetWordId(target);
        r.setRelationType(type);
        repo.save(r);
    }
    
    private void createSampleReviewRecords(ReviewRecordRepository repo, Long userId) {
        // 创建一些已复习的记录（用于展示统计数据）
        LocalDateTime now = LocalDateTime.now();
        
        // 已掌握的单词（复习结果为 KNOWN）
        ReviewRecord r1 = new ReviewRecord();
        r1.setUserId(userId);
        r1.setWordId(1L); // happy
        r1.setResult(ReviewRecord.ReviewResult.KNOWN);
        r1.setProficiency(5);
        r1.setNextReviewAt(now.plusDays(7)); // 7天后复习
        r1.setCreatedAt(now.minusDays(1));
        repo.save(r1);
        
        ReviewRecord r2 = new ReviewRecord();
        r2.setUserId(userId);
        r2.setWordId(2L); // joy
        r2.setResult(ReviewRecord.ReviewResult.KNOWN);
        r2.setProficiency(5);
        r2.setNextReviewAt(now.plusDays(7));
        r2.setCreatedAt(now.minusDays(1));
        repo.save(r2);
        
        ReviewRecord r3 = new ReviewRecord();
        r3.setUserId(userId);
        r3.setWordId(3L); // sad
        r3.setResult(ReviewRecord.ReviewResult.KNOWN);
        r3.setProficiency(4);
        r3.setNextReviewAt(now.plusDays(3));
        r3.setCreatedAt(now.minusDays(1));
        repo.save(r3);
        
        // 模糊的单词（复习结果为 VAGUE）- 今天需要复习
        ReviewRecord r4 = new ReviewRecord();
        r4.setUserId(userId);
        r4.setWordId(4L); // excited
        r4.setResult(ReviewRecord.ReviewResult.VAGUE);
        r4.setProficiency(2);
        r4.setNextReviewAt(now.minusHours(2)); // 已经过期，需要复习
        r4.setCreatedAt(now.minusDays(2));
        repo.save(r4);
        
        ReviewRecord r5 = new ReviewRecord();
        r5.setUserId(userId);
        r5.setWordId(5L); // angry
        r5.setResult(ReviewRecord.ReviewResult.VAGUE);
        r5.setProficiency(2);
        r5.setNextReviewAt(now.minusHours(1)); // 已经过期，需要复习
        r5.setCreatedAt(now.minusDays(2));
        repo.save(r5);
        
        // 不认识的单词（复习结果为 UNKNOWN）- 今天需要复习
        ReviewRecord r6 = new ReviewRecord();
        r6.setUserId(userId);
        r6.setWordId(6L); // apple
        r6.setResult(ReviewRecord.ReviewResult.UNKNOWN);
        r6.setProficiency(1);
        r6.setNextReviewAt(now.minusHours(3)); // 已经过期，需要复习
        r6.setCreatedAt(now.minusDays(1));
        repo.save(r6);
        
        // 今天的复习记录
        ReviewRecord r7 = new ReviewRecord();
        r7.setUserId(userId);
        r7.setWordId(7L); // fruit
        r7.setResult(ReviewRecord.ReviewResult.KNOWN);
        r7.setProficiency(5);
        r7.setNextReviewAt(now.plusDays(7));
        r7.setCreatedAt(now.minusHours(1));
        repo.save(r7);
        
        ReviewRecord r8 = new ReviewRecord();
        r8.setUserId(userId);
        r8.setWordId(8L); // bread
        r8.setResult(ReviewRecord.ReviewResult.KNOWN);
        r8.setProficiency(4);
        r8.setNextReviewAt(now.plusDays(5));
        r8.setCreatedAt(now.minusMinutes(30));
        repo.save(r8);
    }
    
    private void createSampleQuizRecords(QuizRecordRepository repo, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // 测验记录1：优秀
        QuizRecord q1 = new QuizRecord();
        q1.setUserId(userId);
        q1.setScore(90);
        q1.setCorrectCount(9);
        q1.setTotalCount(10);
        q1.setDuration(120);
        q1.setWrongWordIds("15"); // drink
        q1.setCreatedAt(now.minusDays(2));
        repo.save(q1);
        
        // 测验记录2：良好
        QuizRecord q2 = new QuizRecord();
        q2.setUserId(userId);
        q2.setScore(80);
        q2.setCorrectCount(8);
        q2.setTotalCount(10);
        q2.setDuration(150);
        q2.setWrongWordIds("12,14"); // fast, eat
        q2.setCreatedAt(now.minusDays(1));
        repo.save(q2);
        
        // 测验记录3：今天的测验
        QuizRecord q3 = new QuizRecord();
        q3.setUserId(userId);
        q3.setScore(100);
        q3.setCorrectCount(10);
        q3.setTotalCount(10);
        q3.setDuration(90);
        q3.setWrongWordIds("");
        q3.setCreatedAt(now.minusHours(2));
        repo.save(q3);
    }
}
