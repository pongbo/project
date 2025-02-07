package com.java.project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 * {"title":"水是液体？","type":1,"optionList":[{"id":"A","content":"对"},{"id":"B","content":"错"}],"answer":"A","userAnswer":"","userFavor":false,"explain":"难到是固体不成？"},
 * {"title":"电流分有？","type":2,"optionList":[{"id":"A","content":"直流"},{"id":"B","content":"交流"},{"id":"C","content":"直流和交流"}],"answer":"C","userAnswer":"","userFavor":false,"explain":"科技学依据"},
 *  {"title":"酸菜鱼的味道？","type":3,"optionList":[{"id":"A","content":"咸味"},{"id":"B","content":"辣味"},{"id":"C","content":"甜味"},{"id":"D","content":"酸味"}],"answer":"A,B,D","userAnswer":"","userFavor":false,"explain":"你怎么想都行，要的就是这个味，答案只能选A,B,D"},
 *  {"title":"床前（____）光，疑是地上霜。","type":4,"optionList":[{"id":"","content":""}],"answer":"明月","userAnswer":"","userFavor":false,"explain":"问答题没有选项，无法做答，且不参与计分"},
 *   {"title":"什么美国要限制华为？","type":5,"optionList":[{"id":"","content":""}],"answer":"","userAnswer":"","userFavor":false,"explain":"问答题没有选项，无法做答，且不参与计分"},
 * **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionVo {
    Integer qid;
    String title;
    Integer type;
    List<Option> optionList;
    String answer;
    String userAnswer;
    Boolean userFavor;
    String explain;

}
