package io.github.haappi;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.Shape;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Utils {
    public static byte[] stringToByteArray(String message, String delim) {
        String[] msg = message.split(delim);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String s : msg) {
            out.writeUTF(s);
        }

        return out.toByteArray();
    }

    public static byte[] stringToByteArray(String message) {
        return stringToByteArray(message, ";");
    }

    public static Entity raytrace(Player player, double distance) {
        Instance instance = player.getInstance();
        if (instance == null) {
            return null;
        }

        Pos start = player.getPosition();
        Vec direction = player.getPosition().direction();

        double halfAngle = Math.toRadians(20); // Half of the 30-degree cone angle
        double coneHeight = Math.tan(halfAngle) * distance;

        Vec endPoint = Vec.fromPoint(start.add(direction.mul(distance)));

        System.out.println(instance.getNearbyEntities(start, distance));

        Optional<Entity> nearby = instance.getNearbyEntities(start, distance).stream()
                .filter(entity -> entity != player)
                .filter(entity -> isEntityInCone(entity, start, direction, endPoint, coneHeight))
                .min(Comparator.comparingDouble(entity -> entity.getDistanceSquared(player)));

        return nearby.orElse(null);
    }

    private static boolean isEntityInCone(Entity entity, Pos start, Vec direction,
                                          Vec endPoint, double coneHeight) {
        Vec entityPosition = entity.getPosition().asVec();
        Vec startToEnd = endPoint.sub(start.asVec());
        Vec startToEntity = entityPosition.sub(start.asVec());

        // Calculate dot product to determine if entity is within cone angle
        double dotProduct = startToEnd.normalize().dot(startToEntity.normalize());
        if (dotProduct < Math.cos(coneHeight)) {
            return false;
        }

        // Calculate the projected distance of the entity along the ray direction
        double projectedDistance = startToEntity.dot(startToEnd.normalize());
        if (projectedDistance < 0 || projectedDistance > 5.0) {
            return false;
        }

        // Calculate the height of the cone at the projected distance
        double coneHeightAtDistance = projectedDistance * Math.tan(coneHeight);

        // Check if the entity's position is within the cone height at the projected distance
        return Math.abs(startToEntity.y()) <= coneHeightAtDistance;
    }

}
