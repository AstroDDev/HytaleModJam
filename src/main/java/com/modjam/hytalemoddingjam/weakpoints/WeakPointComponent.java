package com.modjam.hytalemoddingjam.weakpoints;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.modjam.hytalemoddingjam.MainPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import javax.swing.text.html.parser.Entity;

public class WeakPointComponent implements Component<EntityStore> {
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;
    public boolean weak;

    public WeakPointComponent(){}

    public WeakPointComponent(float _minX, float _minY, float _minZ, float _maxX, float _maxY, float _maxZ, boolean _weak){
        minX = _minX;
        minY = _minY;
        minZ = _minZ;
        maxX = _maxX;
        maxY = _maxY;
        maxZ = _maxZ;
        weak = _weak;
    }

    public WeakPointComponent(WeakPointComponent component){
        this.minX = component.minX;
        this.minY = component.minY;
        this.minZ = component.minZ;
        this.maxX = component.maxX;
        this.maxY = component.maxY;
        this.maxZ = component.maxZ;
        this.weak = component.weak;
    }

    @Nonnull
    public static ComponentType<EntityStore, WeakPointComponent> getComponentType(){
        return MainPlugin.getInstance().weakPointComponentType;
    }

    public boolean isHit(Ref<EntityStore> targetRef, Ref<EntityStore> projectileRef, Store<EntityStore> store){
        World world = store.getExternalData().getWorld();

        TransformComponent targetTransform = store.getComponent(targetRef, TransformComponent.getComponentType());
        TransformComponent projectileTransform = store.getComponent(projectileRef, TransformComponent.getComponentType());

        Vector3d projectilePosition = projectileTransform.getPosition();

        Vector3d targetPosition = targetTransform.getPosition();
        Vector3f targetRotation = targetTransform.getRotation();

        double angleTo = Math.atan2(projectilePosition.z - targetPosition.z, projectilePosition.x - targetPosition.x);
        double distanceTo = Math.sqrt(Math.pow(projectilePosition.z - targetPosition.z, 2) + Math.pow(projectilePosition.x - targetPosition.x, 2));

        Vector3d localProjPos = new Vector3d(
                Math.cos(angleTo + targetRotation.y) * distanceTo,
                projectilePosition.y - targetPosition.y,
                Math.sin(angleTo + targetRotation.y) * distanceTo
        );

        return (localProjPos.x >= minX && localProjPos.x <= maxX && localProjPos.y >= minY && localProjPos.y <= maxY && localProjPos.z >= minZ && localProjPos.z <= maxZ) == weak;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new WeakPointComponent(this);
    }

    public static final BuilderCodec<WeakPointComponent> CODEC = ((BuilderCodec.Builder<WeakPointComponent>)(BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)((BuilderCodec.Builder<WeakPointComponent>)BuilderCodec.builder(WeakPointComponent.class, WeakPointComponent::new)
            .append(new KeyedCodec("MinX", Codec.FLOAT), (weakPoint, minX) -> weakPoint.minX = minX, (weakPoint) -> weakPoint.minX).add())
            .append(new KeyedCodec("MinY", Codec.FLOAT), (weakPoint, minY) -> weakPoint.minY = minY, (weakPoint) -> weakPoint.minY).add())
            .append(new KeyedCodec("MinZ", Codec.FLOAT), (weakPoint, minZ) -> weakPoint.minZ = minZ, (weakPoint) -> weakPoint.minZ).add())

            .append(new KeyedCodec("MaxX", Codec.FLOAT), (weakPoint, maxX) -> weakPoint.maxX = maxX, (weakPoint) -> weakPoint.maxX).add())
            .append(new KeyedCodec("MaxY", Codec.FLOAT), (weakPoint, maxY) -> weakPoint.maxY = maxY, (weakPoint) -> weakPoint.maxY).add())
            .append(new KeyedCodec("MaxZ", Codec.FLOAT), (weakPoint, maxZ) -> weakPoint.maxZ = maxZ, (weakPoint) -> weakPoint.maxZ).add())

            .append(new KeyedCodec("Weak", Codec.BOOLEAN), (weakPoint, weak) -> weakPoint.weak = weak, (weakPoint) -> weakPoint.weak).add()).build();
}
