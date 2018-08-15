package cz.tefek.botdiril.sql;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class SQLStatementLoader
{
    public static String getCode(String id)
    {
        var dir = new File("static/sql");

        if (!dir.isDirectory())
        {
            dir.mkdirs();
            return null;
        }

        try
        {
            var read = Files.readAllLines(new File(dir, id + ".sql").toPath()).stream().collect(Collectors.joining("\n"));

            return read;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
