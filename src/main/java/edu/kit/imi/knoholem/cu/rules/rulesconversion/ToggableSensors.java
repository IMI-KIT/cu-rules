package edu.kit.imi.knoholem.cu.rules.rulesconversion;

import java.util.HashSet;
import java.util.Set;

/**
 * Lists the sensors with a binary value.
 *
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
enum ToggableSensors {
    OccupancySensor,
    OpeningSensor,

    LightingSetPoint,
    WindowSetPoint,
    ShadingSetPoint;

    public static Set<String> names() {
        Set<String> names = new HashSet<String>();

        for (ToggableSensors value : ToggableSensors.values()) {
            names.add(value.name());
        }

        return names;
    }
}
