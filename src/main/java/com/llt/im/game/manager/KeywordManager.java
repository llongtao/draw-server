package com.llt.im.game.manager;

import com.llt.im.game.model.KeyWord;
import com.llt.im.utils.StringUtils;
import javassist.compiler.ast.Keyword;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class KeywordManager {
    private static final int RANDOM_MAX_TIME = 10000;
    private static Random random = new Random();
    private static final LinkedHashMap<String, KeyWord> KEYWORD_MAP = new LinkedHashMap<>();
    private static final List<String> KEYWORD_LIST = new ArrayList<>();

    static {
        ClassPathResource classPathResource = new ClassPathResource("words.txt");
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = classPathResource.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                if (StringUtils.isNotBlank(s)) {
                    String[] split = s.split(",");
                    if (split.length > 1) {
                        KEYWORD_MAP.put(split[0], new KeyWord(split[0], split[1]));
                        KEYWORD_LIST.add(split[0]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignore) {

                }
            }
        }
    }


    public static List<KeyWord> getKeyWords(int num) {
        int size = KEYWORD_LIST.size();
        List<KeyWord> keyWordList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            KeyWord keyWord = KEYWORD_MAP.get(KEYWORD_LIST.get(random.nextInt(size)));
            while (keyWordList.contains(keyWord)) {
                keyWord = KEYWORD_MAP.get(KEYWORD_LIST.get(random.nextInt(size)));
            }
            keyWordList.add(keyWord);
        }

        return keyWordList;
    }


    public static KeyWord getKeyWord(String word) {
        return KEYWORD_MAP.get(word);
    }


    public static List<KeyWord> getRobotGuessKeyWords(KeyWord key) {
        String desc = key.getDesc();
        int len = key.getLen();
        List<KeyWord> lenKeywords = KEYWORD_MAP.values().stream().filter(item -> item.getName().length() == len).collect(Collectors.toList());
        List<KeyWord> keyWordList = new ArrayList<>();
        getRandom(lenKeywords, 3, keyWordList);

        List<KeyWord> descKeywords = KEYWORD_MAP.values().stream().filter(item -> item.getName().length() == len && Objects.equals(item.getDesc(), desc)).collect(Collectors.toList());
        getRandom(descKeywords, 5, keyWordList);
        keyWordList.add(key);
        return keyWordList;
    }

    private static <T> void getRandom(List<T> list, int size, List<T> target) {
        int len = list.size();
        if (target == null) {
            target = new ArrayList<>();
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 1000; j++) {
                T o = list.get(random.nextInt(len));
                if (!target.contains(o)) {
                    target.add(o);
                    break;
                }
            }
        }
    }
}
