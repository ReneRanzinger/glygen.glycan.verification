package org.glygen.glycan.verification.util;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Modification;
import org.eurocarbdb.MolecularFramework.sugar.ModificationType;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserNodes;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoVisitorReducingEnd implements GlycoVisitor
{
    private boolean m_hasFreeEnd = true;

    @Override
    public void clear()
    {
        this.m_hasFreeEnd = true;
    }

    @Override
    public GlycoTraverser getTraverser(GlycoVisitor a_visitor) throws GlycoVisitorException
    {
        return new GlycoTraverserNodes(a_visitor);
    }

    @Override
    public void start(Sugar a_sugar) throws GlycoVisitorException
    {
        this.clear();
        GlycoTraverser t_traverser = this.getTraverser(this);
        t_traverser.traverseGraph(a_sugar);
    }

    @Override
    public void visit(Monosaccharide a_ms) throws GlycoVisitorException
    {
        if (a_ms.getParentEdge() == null)
        {
            // reducing end
            for (Modification t_mod : a_ms.getModification())
            {
                if (t_mod.getModificationType().equals(ModificationType.ALDI))
                {
                    this.m_hasFreeEnd = false;
                }
            }
        }
    }

    @Override
    public void visit(NonMonosaccharide a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SugarUnitRepeat a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Substituent a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SugarUnitCyclic a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SugarUnitAlternative a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(UnvalidatedGlycoNode a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(GlycoEdge a_arg0) throws GlycoVisitorException
    {
        // TODO Auto-generated method stub

    }

    public boolean hasFreeEnd()
    {
        return m_hasFreeEnd;
    }

}
