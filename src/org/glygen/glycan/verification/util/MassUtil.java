package org.glygen.glycan.verification.util;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.GlycoCTParser;
import org.eurocarbdb.application.glycanbuilder.IonCloud;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;
import org.glygen.glycan.verification.om.GlyGenGlycan;

public class MassUtil
{
    public static final Double DEVIATION = 5.0D;

    protected GlycanWorkspace m_gwb = new GlycanWorkspace(null, false, new GlycanRendererAWT());
    private MonosaccharideConversion m_msdb = null;
    private MassOptions m_massOptions = null;
    private ResidueType m_residueFreeEnd = null;
    private ResidueType m_residueRedEnd = null;

    public MassUtil()
    {
        Config t_objConf = new Config();
        this.m_msdb = new MonosaccharideConverter(t_objConf);

        this.m_massOptions = new MassOptions();
        this.m_massOptions.setDerivatization(MassOptions.NO_DERIVATIZATION);
        this.m_massOptions.setIsotope(MassOptions.ISOTOPE_MONO);
        this.m_massOptions.ION_CLOUD = new IonCloud();
        this.m_massOptions.NEUTRAL_EXCHANGES = new IonCloud();
        this.m_residueFreeEnd = ResidueDictionary.findResidueType("freeEnd");
        this.m_residueRedEnd = ResidueDictionary.findResidueType("redEnd");
    }

    public void fillMass(GlyGenGlycan a_glycan) throws GlycoVisitorException, SugarImporterException
    {
        // parse glycoCT
        SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
        Sugar t_sugar = t_importer.parse(a_glycan.getGlycoCT());
        Boolean t_freeEnd = this.hasFreeEnd(t_sugar);
        // calculate mass
        Glycan t_glycan = null;
        double t_mass = 0;
        double t_massPme = 0;
        try
        {
            t_glycan = this.sugarToGlycan(t_sugar, a_glycan.getGlycoCT());
            t_mass = this.getMass(t_glycan, MassOptions.NO_DERIVATIZATION, t_freeEnd);
            t_massPme = this.getMass(t_glycan, MassOptions.PERMETHYLATED, t_freeEnd);
            a_glycan.setMass(t_mass);
            a_glycan.setMassPme(t_massPme);
        }
        catch (Exception e)
        {
            throw new GlycoVisitorException("Unable to load glycan to GWB: " + e.getMessage(), e);
        }
    }

    private Boolean hasFreeEnd(Sugar a_sugar) throws GlycoVisitorException
    {
        GlycoVisitorReducingEnd t_visitor = new GlycoVisitorReducingEnd();
        t_visitor.start(a_sugar);
        return t_visitor.hasFreeEnd();
    }

    private double getMass(Glycan a_glycan, String a_derivatization, boolean a_freeEnd)
    {
        this.m_massOptions.setDerivatization(a_derivatization);
        if (a_freeEnd)
        {
            this.m_massOptions.setReducingEndType(this.m_residueFreeEnd);
        }
        else
        {
            this.m_massOptions.setReducingEndType(m_residueRedEnd);
        }
        a_glycan.setMassOptions(this.m_massOptions);
        return a_glycan.computeMass();
    }

    private Glycan sugarToGlycan(Sugar a_sugar, String a_glycoCT) throws Exception
    {
        if (a_glycoCT.contains("n-sulfate"))
        {
            throw new Exception("Unrecognized residue: n-sulfate");
        }
        GlycoVisitorFromGlycoCT t_visFromGlycoCT = new GlycoVisitorFromGlycoCT(this.m_msdb);
        t_visFromGlycoCT.setNameScheme(GlycanNamescheme.GWB);
        Glycan t_glycan = GlycoCTParser.fromSugar(a_sugar, this.m_msdb, t_visFromGlycoCT, new MassOptions(), false);
        String t_sequence = t_glycan.toString();
        t_sequence = t_sequence.replace("2D-Neu,p(--9?1NAc)--5?1NAc", "2D-NeuAc,p--9?1NAc");
        return Glycan.fromString(t_sequence);
    }

    public boolean sameMass(Double a_massGT, Double a_massGWB)
    {
        if (a_massGT == null)
        {
            return false;
        }
        Double t_delta = (a_massGT * MassUtil.DEVIATION) / 1000000.0D;
        if ((a_massGT - t_delta) < a_massGWB && (a_massGT + t_delta) > a_massGWB)
        {
            return true;
        }
        return false;
    }
}
