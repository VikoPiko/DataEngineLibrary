package TaskClasses;

import com.viko.annotations.*;

@FileSource(delimiter = ",")
public class Customer {

    @NotNull
    @Column(index = 0, name = "name")
    private String name;
    @NotNull
    @Regex(pattern = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    @Column(index = 1, name = "email")
    private String email;
    @NotNull
    /// 120 years should be enough for here...
    @Range(min = 18, max = 122) //age of longest living person, just in case :')
    @Column(index = 2, name = "age")
    private int age;
}
