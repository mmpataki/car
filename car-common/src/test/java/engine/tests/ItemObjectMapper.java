package engine.tests;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static engine.tests.Item.*;

public class ItemObjectMapper {

    private static final Logger LOGGER = Logger
            .getLogger(ItemObjectMapper.class);

    private final Map<String, Class<?>> classModel;

    public ItemObjectMapper(Map<String, Class<?>> classModel) {
        this.classModel = classModel;
    }

    public Object parse(Item item) {
        return parse(item, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object parse(Item item, Class<?> desiredType) {
        LOGGER.info("desired type: " + desiredType + ", item-type: "
                + item.getClass());
        try {
            if (item instanceof ObjectItem) {
                String type = ((ObjectItem) item).getType();
                LOGGER.debug("type: " + type);
                if (!classModel.containsKey(type)) {
                    throw new IllegalStateException("Cannot map type: " + type);
                }
                Class<?> clazz = classModel.get(type);
                Object instance = clazz.newInstance();
                for (Map.Entry<String, Item> entry : ((ObjectItem) item)
                        .getAttributes().entrySet()) {
                    Field field = clazz.getDeclaredField(entry.getKey());
                    LOGGER.debug("parsing for field: " + field.getName()
                            + " | " + field);
                    field.setAccessible(true);
                    field.set(instance,
                            parse(entry.getValue(), field.getType()));
                }
                return instance;
            } else if (item instanceof ListItem) {
                ArrayList<Object> result = new ArrayList<Object>();
                for (Item value : ((ListItem) item).getValues()) {
                    result.add(parse(value, Object.class)); // TODO
                }
                return result;
            } else if (item instanceof ValueItem) {

                if (((ValueItem) item).isNullOrEmpty()) {
                    return null;
                }

                if (desiredType.isAssignableFrom(int.class)) {
                    return Integer.parseInt(item.getStringRepresentation());
                }
                if (desiredType.isAssignableFrom(double.class)) {
                    return Double.parseDouble(item.getStringRepresentation());
                }
                if (desiredType.isAssignableFrom(long.class)
                        || desiredType.isAssignableFrom(Long.class)) {
                    return Long.parseLong(item.getStringRepresentation());
                }
                if (desiredType.isAssignableFrom(boolean.class)) {
                    return Boolean.parseBoolean(item.getStringRepresentation());
                }
                if (desiredType.isAssignableFrom(String.class)) {
                    return item.getStringRepresentation();
                }
                if (desiredType.isAssignableFrom(Date.class)) {
                    return null; // TODO
                }
                if (Enum.class.isAssignableFrom(desiredType)) {
                    return Enum.valueOf((Class<Enum>) desiredType,
                            item.getStringRepresentation());
                }

                throw new IllegalStateException(
                        "Could not assign value of type=" + desiredType
                                + " (value=" + item.getStringRepresentation()
                                + ")");

            } else {
                throw new IllegalStateException("Item of unexpected type: "
                        + item);
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected exception!", e);
            throw new IllegalStateException("Unexpected Exception! (item="
                    + item + ", desiredType=" + desiredType + ")", e);
        }
    }
}
