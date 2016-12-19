package ua.com.integer.gdx.xml.ui.creator.imp;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectMap;

import ua.com.integer.gdx.xml.ui.XUIElement;
import ua.com.integer.gdx.xml.ui.XUI;
import ua.com.integer.gdx.xml.ui.creator.XUICreator;

public class LinkedXUICreator extends XUICreator {
    @Override
    protected Actor create(String packageName) {
        ObjectMap<String, String> originalAttributes = new ObjectMap<>(element.attributes);

        XUIElement linkedDef = XUI.get(element.attributes.get("path"));
        linkedDef.copyTo(element);
        element.attributes.putAll(originalAttributes);

        return XUICreator.createActor(element.name, element);
    }
}