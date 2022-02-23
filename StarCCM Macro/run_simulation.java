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
import star.flow.*;

public class run_simulation extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Map<Integer, Double> map = new HashMap<Integer, Double>();
      map.put(50, 0.00099025);
      map.put(100, 0.00082517);
      map.put(150, 0.00067884);
      map.put(200, 0.00055125);

    Map<Integer, Double> pressure = new HashMap<Integer, Double>();
    pressure.put(300, -8.83516e-6);
    pressure.put(600, -3.534063e-5);
    pressure.put(900, -79.51643e-6);
    pressure.put(1200, -141.36254e-6);

    Map<String, String> turbulence_model = new HashMap<String, String>();
    turbulence_model.put("Physics 1", "Standard k-epsilon");
    turbulence_model.put("Physics 2", "Standard low-Re linear");
    turbulence_model.put("Physics 3", "Standard low-Re quadratic");
    turbulence_model.put("Physics 4", "Reynolds stress turbulence");

    for (Map.Entry<Integer, Double> entry : map.entrySet()) {
      int grid = entry.getKey();
      double value = entry.getValue();

      for (Map.Entry<Integer, Double> p_entry : pressure.entrySet()) {
        int Re = p_entry.getKey();
        double pressure_value = p_entry.getValue();

        for (Map.Entry<String, String> turb : turbulence_model.entrySet()) {
          String model = turb.getKey();
          String model_name = turb.getValue();

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

          BoundaryInterface boundaryInterface_0 = ((BoundaryInterface) simulation_0.getInterfaceManager().getInterface("Interface 1"));
      
          PressureJump pressureJump_0 = 
            boundaryInterface_0.getValues().get(PressureJump.class);
      
          pressureJump_0.getPressureJump().setValue(pressure_value);

          Region region_0 = simulation_0.getRegionManager().getRegion("Region");

          PhysicsContinuum physicsContinuum = 
          ((PhysicsContinuum) simulation_0.getContinuumManager().getContinuum(model));
    
          simulation_0.getContinuumManager().addToContinuum(new NeoObjectVector(new Object[] {region_0}), physicsContinuum);
          
          simulation_0.getSimulationIterator().runAutomation();

          ResidualPlot residualPlot_0 = 
            ((ResidualPlot) simulation_0.getPlotManager().getPlot("Residuals"));

          PlotUpdate plotUpdate_0 = 
            residualPlot_0.getPlotUpdate();

            residualPlot_0.export(String.format("Outputs/%s/Re%s/residuals_%s.csv", model_name, String.valueOf(Re), String.valueOf(grid)), ",");

          HardcopyProperties hardcopyProperties_6 = 
            plotUpdate_0.getHardcopyProperties();

          hardcopyProperties_6.setCurrentResolutionWidth(1764);

          hardcopyProperties_6.setCurrentResolutionWidth(1763);

          hardcopyProperties_6.setCurrentResolutionHeight(870);

          XyzInternalTable xyzInternalTable_0 = 
            ((XyzInternalTable) simulation_0.getTableManager().getTable("XYZ Internal Table"));

          xyzInternalTable_0.extract();

          xyzInternalTable_0.export(String.format("Outputs/%s/Re%s/data_%s.csv", model_name, String.valueOf(Re), String.valueOf(grid)), ",");

          Cartesian2DAxisManager cartesian2DAxisManager_0 = 
            ((Cartesian2DAxisManager) residualPlot_0.getAxisManager());

          cartesian2DAxisManager_0.setAxesBounds(new Vector(Arrays.asList(new star.common.AxisManager.AxisBounds("Bottom Axis", 1.0, false, 2478.0, false), new star.common.AxisManager.AxisBounds("Left Axis", 2.957874090299894E-13, false, 1.7436383783867537, false))));

          Solution solution_0 = 
            simulation_0.getSolution();

          solution_0.clearSolution(Solution.Clear.History, Solution.Clear.Fields, Solution.Clear.LagrangianDem);
        }
      }
    }
  }
}
