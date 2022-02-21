// STAR-CCM+ macro: run_simulation.java
// Written by STAR-CCM+ 13.04.011
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.sweptmesher.ui.*;
import star.vis.*;
import star.meshing.*;
import star.sweptmesher.*;

public class run_simulation extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {
    Map<Integer, Double> map = new HashMap<Integer, Double>();
    map.put(65, 0.000735121);
    map.put(75, 0.0006530360906741604);
    map.put(85, 0.0005808561774881047);
    map.put(95, 0.0005178742247166794);
    map.put(105, 0.0004633831966347307);
    map.put(115, 0.0004166760575171052);
    map.put(125, 0.0003770457716386496);

    for (Map.Entry<Integer, Double> entry : map.entrySet()) {
      int grid = entry.getKey();
      double value = entry.getValue();

      Simulation simulation_0 = 
        getActiveSimulation();

      Scene scene_8 = 
        simulation_0.getSceneManager().createScene("Directed Mesh");

      scene_8.initializeAndWait();

      DirectedMeshOperation directedMeshOperation_5 = 
        ((DirectedMeshOperation) simulation_0.get(MeshOperationManager.class).getObject("Directed Mesh"));

      directedMeshOperation_5.editDirectedMeshOperation(scene_8);

      PolygonMeshDisplayer polygonMeshDisplayer_7 = 
        ((PolygonMeshDisplayer) scene_8.getDisplayerManager().getDisplayer("PolygonMesh displayer 1"));

      polygonMeshDisplayer_7.initialize();

      PartDisplayer partDisplayer_37 = 
        ((PartDisplayer) scene_8.getDisplayerManager().getDisplayer("Dummy 1"));

      partDisplayer_37.initialize();

      PartDisplayer partDisplayer_38 = 
        ((PartDisplayer) scene_8.getDisplayerManager().getDisplayer("SourceSurfaces 1"));

      partDisplayer_38.initialize();

      PartDisplayer partDisplayer_39 = 
        ((PartDisplayer) scene_8.getDisplayerManager().getDisplayer("TargetSurfaces 1"));

      partDisplayer_39.initialize();

      PartDisplayer partDisplayer_40 = 
        ((PartDisplayer) scene_8.getDisplayerManager().getDisplayer("InternalSurfaces 1"));

      partDisplayer_40.initialize();

      PartDisplayer partDisplayer_41 = 
        ((PartDisplayer) scene_8.getDisplayerManager().getDisplayer("DirectedVolumeMesh 1"));

      partDisplayer_41.initialize();

      scene_8.open();

      scene_8.setAdvancedRenderingEnabled(false);

      SceneUpdate sceneUpdate_8 = 
        scene_8.getSceneUpdate();

      HardcopyProperties hardcopyProperties_10 = 
        sceneUpdate_8.getHardcopyProperties();

      hardcopyProperties_10.setCurrentResolutionWidth(25);

      hardcopyProperties_10.setCurrentResolutionHeight(25);

      Scene scene_6 = 
        simulation_0.getSceneManager().getScene("Mesh Scene 1");

      SceneUpdate sceneUpdate_6 = 
        scene_6.getSceneUpdate();

      HardcopyProperties hardcopyProperties_8 = 
        sceneUpdate_6.getHardcopyProperties();

      hardcopyProperties_8.setCurrentResolutionWidth(1765);

      hardcopyProperties_8.setCurrentResolutionHeight(871);

      hardcopyProperties_10.setCurrentResolutionWidth(1763);

      hardcopyProperties_10.setCurrentResolutionHeight(870);

      scene_8.resetCamera();

      DirectedMeshDisplayController directedMeshDisplayController_5 = 
        directedMeshOperation_5.getDisplayController();

      directedMeshDisplayController_5.getHighlightedSurfaces().setQuery(null);

      directedMeshDisplayController_5.getHighlightedSurfaces().setObjects();

      directedMeshDisplayController_5.getHiddenSurfaces().setQuery(null);

      directedMeshDisplayController_5.getHiddenSurfaces().setObjects();

      DirectedPatchSourceMesh directedPatchSourceMesh_3 = 
        ((DirectedPatchSourceMesh) directedMeshOperation_5.getGuidedSurfaceMeshBaseManager().getObject("Patch Mesh"));

      directedPatchSourceMesh_3.editDirectedPatchSourceMesh();

      CurrentView currentView_7 = 
        scene_8.getCurrentView();

      currentView_7.setInput(new DoubleVector(new double[] {0.0, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-1.4177446879808873, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-1.0, 0.0, 0.0}), 0.7088723439378913, 1);

      currentView_7.setInput(new DoubleVector(new double[] {0.0, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-2.7320508075688776, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-0.0, -1.0, 0.0}), 0.7071067811865476, 1);

      scene_8.setTransparencyOverrideMode(SceneTransparencyOverride.USE_DISPLAYER_PROPERTY);

      scene_8.setAdvancedRenderingEnabled(false);

      directedPatchSourceMesh_3.autopopulateFeatureEdges();

      Units units_0 = 
        simulation_0.getUnitsManager().getPreferredUnits(new IntVector(new int[] {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));

      directedPatchSourceMesh_3.enablePatchMeshMode();

      currentView_7.setInput(new DoubleVector(new double[] {0.0, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-2.7320508075688776, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {0.0, -1.0, 0.0}), 0.7071067811865476, 1);

      PatchCurve patchCurve_4 = 
        ((PatchCurve) directedPatchSourceMesh_3.getPatchCurveManager().getObject("PatchCurve 2"));

      patchCurve_4.getStretchingFunction().setSelected(StretchingFunctionBase.Type.ONE_SIDED_HYPERBOLIC);

      directedPatchSourceMesh_3.defineMeshPatchCurve(patchCurve_4, patchCurve_4.getStretchingFunction(), value, 0.1, grid, false, false);

      currentView_7.setInput(new DoubleVector(new double[] {0.0, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {-2.7320508075688776, 0.5, 0.5000000000000001}), new DoubleVector(new double[] {0.0, -1.0, 0.0}), 0.7071067811865476, 1);

      PatchCurve patchCurve_5 = 
        ((PatchCurve) directedPatchSourceMesh_3.getPatchCurveManager().getObject("PatchCurve 0"));

      patchCurve_5.getStretchingFunction().setSelected(StretchingFunctionBase.Type.ONE_SIDED_HYPERBOLIC);

      directedPatchSourceMesh_3.defineMeshPatchCurve(patchCurve_5, patchCurve_5.getStretchingFunction(), value, 0.1, grid, false, false);

      directedPatchSourceMesh_3.stopEditPatchOperation();

      scene_8.setAdvancedRenderingEnabled(false);

      scene_8.setAdvancedRenderingEnabled(false);

      scene_8.setAdvancedRenderingEnabled(false);

      scene_8.setAdvancedRenderingEnabled(false);

      directedMeshOperation_5.stopEditingDirectedMeshOperation();

      simulation_0.getSceneManager().deleteScenes(new NeoObjectVector(new Object[] {scene_8}));

      hardcopyProperties_8.setCurrentResolutionWidth(1763);

      hardcopyProperties_8.setCurrentResolutionHeight(870);
      directedMeshOperation_5.execute();
      simulation_0.getSimulationIterator().runAutomation();

      ResidualPlot residualPlot_0 = 
        ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));

      PlotUpdate plotUpdate_0 = 
        residualPlot_0.getPlotUpdate();

      HardcopyProperties hardcopyProperties_6 = 
        plotUpdate_0.getHardcopyProperties();

      hardcopyProperties_6.setCurrentResolutionWidth(1764);

      hardcopyProperties_6.setCurrentResolutionWidth(1763);

      hardcopyProperties_6.setCurrentResolutionHeight(870);

      XyzInternalTable xyzInternalTable_0 = 
        ((XyzInternalTable) simulation_0.getTableManager().getTable("XYZ Internal Table"));

      xyzInternalTable_0.extract();

      xyzInternalTable_0.export(String.format("C:\\Users\\howar\\Desktop\\CFD Project Assignment 2\\data_%s.csv", String.valueOf(grid)), ",");

      Cartesian2DAxisManager cartesian2DAxisManager_0 = 
        ((Cartesian2DAxisManager) residualPlot_0.getAxisManager());

      cartesian2DAxisManager_0.setAxesBounds(new Vector(Arrays.asList(new star.common.AxisManager.AxisBounds("Bottom Axis", 1.0, false, 2478.0, false), new star.common.AxisManager.AxisBounds("Left Axis", 2.957874090299894E-13, false, 1.7436383783867537, false))));

      Solution solution_0 = 
        simulation_0.getSolution();

      solution_0.clearSolution(Solution.Clear.History, Solution.Clear.Fields, Solution.Clear.LagrangianDem);
    }
  }
}
