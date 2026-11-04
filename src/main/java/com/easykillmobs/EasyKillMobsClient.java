package com.easykillmobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class EasyKillMobsClient implements ClientModInitializer {

    private static KeyBinding guiKey;
    private static KeyBinding disableKey;

    private static boolean enabled = false;
    private static boolean permanentlyDisabled = false;

    private static int delayMs = 500;
    private static long lastAttackAt = 0L;
    private static int comboTicks = 0;

    @Override
    public void onInitializeClient() {

        EasyKillMobsConfig.load();

        guiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.easykillmobs.gui",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "category.easykillmobs"
                )
        );

        disableKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.easykillmobs.disable",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        "category.easykillmobs"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                EasyKillMobsClient::tick
        );
    }

    private static void tick(MinecraftClient client) {

        // V: Open GUI
        while (guiKey.wasPressed()) {

            if (client.player != null && !permanentlyDisabled) {
                client.setScreen(new EasyKillMobsScreen());
            }
        }

        // X: Permanently disable
        while (disableKey.wasPressed()) {

            if (!permanentlyDisabled) {

                permanentlyDisabled = true;
                enabled = false;

                EasyKillMobsConfig.save();

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("EasyKillMobs permanently disabled."),
                            false
                    );
                }
            }
        }

        // O + Backslash: Toggle Auto Attack
        boolean oDown = InputUtil.isKeyPressed(
                client.getWindow().getHandle(),
                GLFW.GLFW_KEY_O
        );

        boolean slashDown = InputUtil.isKeyPressed(
                client.getWindow().getHandle(),
                GLFW.GLFW_KEY_BACKSLASH
        );

        if (oDown && slashDown) {

            comboTicks++;

            if (comboTicks == 1 && !permanentlyDisabled) {

                enabled = !enabled;
                EasyKillMobsConfig.save();

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("EasyKillMobs: " + (enabled ? "ON" : "OFF")),
                            true
                    );
                }
            }

        } else {
            comboTicks = 0;
        }

        // Stop if disabled
        if (!enabled ||
                permanentlyDisabled ||
                client.player == null ||
                client.world == null ||
                client.interactionManager == null) {
            return;
        }

        // Get entity under crosshair
        if (!(client.crosshairTarget instanceof EntityHitResult hit)) {
            return;
        }

        Entity target = hit.getEntity();

        if (!(target instanceof LivingEntity living) ||
                target == client.player ||
                !living.isAlive()) {
            return;
        }

        long now = System.currentTimeMillis();

        if (now - lastAttackAt < delayMs) {
            return;
        }

        // Respect vanilla attack cooldown
        if (client.player.getAttackCooldownProgress(0.0F) < 1.0F) {
            return;
        }

        client.interactionManager.attackEntity(
                client.player,
                target
        );

        client.player.swingHand(Hand.MAIN_HAND);

        lastAttackAt = now;
    }

    static boolean isEnabled() {
        return enabled && !permanentlyDisabled;
    }

    static boolean isPermanentlyDisabled() {
        return permanentlyDisabled;
    }

    static int getDelayMs() {
        return delayMs;
    }

    static void setDelayMs(int value) {

        delayMs = Math.max(100, Math.min(2000, value));

        EasyKillMobsConfig.save();
    }

    static void setEnabled(boolean value) {

        if (!permanentlyDisabled) {
            enabled = value;
            EasyKillMobsConfig.save();
        }
    }

    static void setDelayMsFromConfig(int value) {
        delayMs = Math.max(100, Math.min(2000, value));
    }

    static void setEnabledFromConfig(boolean value) {
        enabled = value;
    }

    static void setPermanentlyDisabledFromConfig(boolean value) {

        permanentlyDisabled = value;

        if (value) {
            enabled = false;
        }
    }
}
