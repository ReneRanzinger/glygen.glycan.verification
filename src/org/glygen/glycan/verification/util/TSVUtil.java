package org.glygen.glycan.verification.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.glygen.glycan.verification.om.TSVEntry;

public class TSVUtil
{
    public static List<TSVEntry> readTSV(String a_file) throws IOException
    {
        List<TSVEntry> t_result = new ArrayList<>();
        // open the file
        BufferedReader t_readerTSV = new BufferedReader(new FileReader(a_file));
        // read header
        String t_line = t_readerTSV.readLine();
        // read first line
        t_line = t_readerTSV.readLine();
        while (t_line != null)
        {
            if (t_line.trim().length() > 0)
            {
                TSVEntry t_entry = TSVUtil.parseLine(t_line);
                t_result.add(t_entry);
                // read next line
                t_line = t_readerTSV.readLine();
            }
        }
        t_readerTSV.close();
        return t_result;
    }

    private static TSVEntry parseLine(String a_line)
    {
        String[] t_parts = a_line.split("\t");
        TSVEntry t_entry = new TSVEntry();
        t_entry.setId(t_parts[0]);
        if (t_parts.length > 2)
        {
            t_entry.setMass(TSVUtil.getMass(t_parts[2]));
        }
        if (t_parts.length > 3)
        {
            t_entry.setMassPme(TSVUtil.getMass(t_parts[3]));
        }
        return t_entry;
    }

    private static Double getMass(String a_value)
    {
        if (a_value.trim().length() == 0)
        {
            return null;
        }
        return Double.parseDouble(a_value.trim());
    }
}
