package com.yhx.log.factory;

import com.yhx.log.pattern.CommonPattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghuaxu
 * @date 2022/6/9 14:26
 */
public class PatternFactory {

    private List<CommonPattern> patternList;

    Map<String, CommonPattern> patternMap;

    public PatternFactory(List<CommonPattern> patternList) {
        this.patternList = patternList;
        this.patternMap = new HashMap<>();
        for (CommonPattern pattern : this.patternList) {
            patternMap.put(pattern.getPatternName(), pattern);
        }
        this.patternList.sort(CommonPattern.PATTERN_COMPARATOR);
    }

    public CommonPattern getPattern(String patternName) {
        return this.patternMap.get(patternName);
    }

    public List<CommonPattern> getPatternList() {
        return patternList;
    }
}
