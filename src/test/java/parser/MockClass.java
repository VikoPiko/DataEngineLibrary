package parser;

import com.viko.annotations.*;

@FileSource(delimiter = ",")
public class MockClass {
    @NotNull(message = "name is required")
    @Column(index = 0, name = "name1")
    public String name;

    @Regex(pattern = ".+@.+\\..+", message = "invalid email")
    @Column(index = 1, name = "email")
    public String email;

    @Range(min = 18, max = 50, message = "Out of range....")
    @Column(index = 2, name = "age")
    public int age;
}
