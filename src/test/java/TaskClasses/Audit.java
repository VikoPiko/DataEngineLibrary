package TaskClasses;

import com.viko.annotations.Column;
import com.viko.annotations.FileSource;
import com.viko.annotations.NotNull;

@FileSource(delimiter = ";")
public class Audit {
    @NotNull
    @Column(index = 0, name = "ipAddress")
    public String ipAddress;

    @NotNull
    @Column(index = 1, name = "severityLevel")
    public int severityLevel;
}
