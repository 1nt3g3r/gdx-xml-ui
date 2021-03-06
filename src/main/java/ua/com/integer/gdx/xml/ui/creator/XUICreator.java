package ua.com.integer.gdx.xml.ui.creator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import ua.com.integer.gdx.xml.ui.creator.imp.ButtonCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.CheckboxCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.GroupCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.HorizontalGroupCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.ImageCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.LabelCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.ActorCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.ScrollPaneCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.SliderCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.TextButtonCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.TouchpadCreator;
import ua.com.integer.gdx.xml.ui.creator.imp.VerticalGroupCreator;
import ua.com.integer.gdx.xml.ui.element.XUIElement;
import ua.com.integer.gdx.xml.ui.res.XUIAssetsAccess;

/**
 * Create actor by his type. Type of actor is just string (like <b>actor</b>, <b>group</b>)
 */
public abstract class XUICreator {
	private static ObjectMap<String, XUICreator> creators = new ObjectMap<String, XUICreator>();

    /**
     * Clear all creators and register creators for predefined actor types (like <b>actor</b>, <b>group</b>, etc)
     */
	public static void init() {
		creators.clear();

		register("Actor", new ActorCreator());
		register("Group", new GroupCreator());
		register("Image", new ImageCreator());
		register("Label", new LabelCreator());
		register("Slider", new SliderCreator());
		register("ScrollPane", new ScrollPaneCreator());
		register("VerticalGroup", new VerticalGroupCreator());
		register("HorizontalGroup", new HorizontalGroupCreator());
		register("Button", new ButtonCreator());
		register("TextButton", new TextButtonCreator());
        register("CheckBox", new CheckboxCreator());
		register("Touchpad", new TouchpadCreator());
	}

    /**
     * Register creator for given type
     */
	public static void register(String type, XUICreator creator) {
		creators.put(type, creator);
	}

	protected XUIElement element;

    /**
     * Implementation to create actor of specified type
     */
	protected abstract Actor create(String type);

	/**
	 * If {@link XUIElement} that under inflating now has attribute with given name
     */
	protected boolean hasAttribute(String attributeName) {
		return element.attributes.containsKey(attributeName);
	}

	/**
	 * Return value of specified attribute for {@link XUIElement} that under inflating now
     */
	protected String getAttribute(String attributeName) {
		return element.attributes.get(attributeName);
	}

    /**
     * Creates actor of given type from given XMLElement.
     *
     * Given XMLElement can contain some user-defined variables (like $myVar$). That variables will be replaced by his values before creating actor.
     *
     * Creation isn't recursive (i.e. if actor is group and has children, that children will not be created, only single actor).
     */
	public static Actor createActor(String type, XUIElement element) {
		XUICreator creator = creators.get(type);
		if (creator != null) {
			creator.element = element;
			replaceVariables(element);
			Actor result = creator.create(type);
			result.setUserObject(element);
			return result;
		}

        throw new IllegalArgumentException("There is no creator for " + type + " type!");
	}

	private static void replaceVariables(XUIElement element) {
		for(String key : element.attributes.keys()) {
			String value = element.attributes.get(key);

			if (value.contains("$")) {
				element.attributes.put(key, replaceVariables(value));
			}
		}
	}

	private static String replaceVariables(String value) {
		Array<String> tmpArray = new Array<>();
		for(int i = 0; i < value.length(); i++) {
			int index1 = value.indexOf("$", i);
			if (index1 != -1) {
				int index2 = value.indexOf("$", index1 + 1);

				i = index2;

				String var = value.substring(index1 + 1, index2);
				tmpArray.add(var);
			}
		}

		for(String var : tmpArray) {
			String varValue = XUIAssetsAccess.getVariable(var);
			value = value.replace("$" + var + "$", varValue);
		}

		return value;
	}
}
