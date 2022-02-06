package com.example.mummoomserver.domain.Likecnt.entity;

import com.example.mummoomserver.config.BaseTimeEntity;
import com.example.mummoomserver.domain.Post.Post;
import com.example.mummoomserver.login.users.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@DynamicInsert
public class Likecnt extends BaseTimeEntity {

    @ApiModelProperty(example = "좋아하는 게시글 목록 Idx")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likecntIdx;

    @ApiModelProperty(example = "유저 불러옴")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", referencedColumnName = "userIdx")
    private User user;

    @ApiModelProperty(example = "게시글 불러옴")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postIdx", referencedColumnName = "postIdx")
    private Post post;

    @ApiModelProperty(example = "상태 코드")
    @Column
    private String status;

    @Builder
    public Likecnt( User user,Post post,String status) {
        this.user = user;
        this.post = post;
        this.status = status;
    }
}
