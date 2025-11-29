package org.example;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammars{

    private final Map<Integer, GrammarDefinition> grammars = new HashMap<>();

    public Grammars(){
        initializeGrammars();
    }

    private void initializeGrammars(){
        grammars.put(1, new Grammar1());
        grammars.put(2, new Grammar2());
        grammars.put(3, new Grammar3());
    }

    public GrammarDefinition getGrammar(int grammarNumber){
        return grammars.get(grammarNumber);
    }
}

interface GrammarDefinition{
    int getRuleCount();
    GrammarRule getRule(int ruleNumber);
    Shape createInitSymbol(double centerX, double centerY, double size);
}

interface GrammarRule{
    boolean canApply(Shape shape, List<Shape> allShapes);
    void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params);
    void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams);
}