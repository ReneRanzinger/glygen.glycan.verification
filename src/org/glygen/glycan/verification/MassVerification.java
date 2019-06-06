package org.glygen.glycan.verification;

import java.io.IOException;
import java.util.List;

import org.glygen.glycan.verification.om.GlyGenGlycan;
import org.glygen.glycan.verification.om.TSVEntry;
import org.glygen.glycan.verification.util.GlyGenApiUtil;
import org.glygen.glycan.verification.util.MassUtil;
import org.glygen.glycan.verification.util.TSVUtil;

public class MassVerification
{

    public static void main(String[] args) throws IOException
    {
        GlyGenApiUtil t_glyGenAPI = new GlyGenApiUtil();
        MassUtil t_massUtil = new MassUtil();
        List<TSVEntry> t_entries = TSVUtil.readTSV("./input/glycan_properties.tsv");
        for (TSVEntry t_tsvEntry : t_entries)
        {
            String t_glycoCT = null;
            try
            {
                t_glycoCT = t_glyGenAPI.getGlycoCT(t_tsvEntry.getId());
            }
            catch (Exception e)
            {
                System.out.println(t_tsvEntry.getId() + "\tUnable to download from GlyGenAPI: " + e.getMessage());
            }
            if (t_glycoCT != null)
            {
                GlyGenGlycan t_glycanInfo = new GlyGenGlycan(t_tsvEntry.getId(), t_glycoCT);
                try
                {
                    t_massUtil.fillMass(t_glycanInfo);
                    if (!(t_massUtil.sameMass(t_tsvEntry.getMass(), t_glycanInfo.getMass())))
                    {
                        System.out.println(t_glycanInfo.getId() + "\tMass missmatch\t" + t_tsvEntry.getMass() + "\t"
                                + t_glycanInfo.getMass());
                    }
                    if (!(t_massUtil.sameMass(t_tsvEntry.getMassPme(), t_glycanInfo.getMassPme())))
                    {
                        System.out.println(t_glycanInfo.getId() + "\tPMe Mass missmatch\t" + t_tsvEntry.getMassPme()
                                + "\t" + t_glycanInfo.getMassPme());
                    }
                }
                catch (Exception e)
                {
                    System.out.println(t_tsvEntry.getId() + "\tUnable to calculate GWB masses: " + e.getMessage());
                }
            }
        }
    }

}
