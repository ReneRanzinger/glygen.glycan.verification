package org.glygen.glycan.verification.om;

public class TSVEntry
{
    private String m_id = null;
    private Double m_mass = null;
    private Double m_massPme = null;

    public String getId()
    {
        return this.m_id;
    }

    public void setId(String a_id)
    {
        this.m_id = a_id;
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
