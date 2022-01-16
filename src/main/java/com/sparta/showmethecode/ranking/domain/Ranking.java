package com.sparta.showmethecode.ranking.domain;

import com.sparta.showmethecode.language.domain.Timestamped;
import com.sparta.showmethecode.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.tool.schema.extract.internal.TableInformationImpl;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int answerCount;
    private double average;
    private double evalTotal;
    private int evalCount;

    @OneToOne(mappedBy = "ranking", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public Ranking(int answerCount, double average, double evalTotal, int evalCount) {
        this.answerCount = answerCount;
        this.average = average;
        this.evalTotal = evalTotal;
        this.evalCount = evalCount;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void evaluate(double point) {
        this.evalCount++;
        this.evalTotal += point;
        this.average = evalTotal / evalCount;
    }

    public void increaseAnswerCount() {
        this.answerCount++;
    }

}
