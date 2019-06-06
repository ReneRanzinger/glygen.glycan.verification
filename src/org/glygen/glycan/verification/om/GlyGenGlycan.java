package org.glygen.glycan.verification.om;

public class GlyGenGlycan
{
    private String m_id = null;
    private String m_glycoCT = null;
    private Double m_mass = null;
    private Double m_massPme = null;

    public GlyGenGlycan(String a_id, String a_glycoCT)
    {
        this.m_id = a_id;
        this.m_glycoCT = a_glycoCT;
    }

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    public String getGlycoCT()
    {
        return this.m_glycoCT;
    }

    public void setGlycoCT(String a_glycoCT)
    {
        this.m_glycoCT = a_glycoCT;
    }

    public Double getMass()
    {
        return this.m_mass;
    }

    public void setMass(Double a_mass)
    {
        this.m_mass = a_mass;
    }

    public Double getMassPme()
    {
        return this.m_massPme;
    }

    public void setMassPme(Double a_massPme)
    {
        this.m_massPme = a_massPme;
    }
}
