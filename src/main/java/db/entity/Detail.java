package db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Detail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String pid;

    private String name;

    private Integer count;

    private Integer col;

    private Integer sort;


}
