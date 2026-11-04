package com.easykillmobs;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EasyKillMobsScreen extends Screen {

    public EasyKillMobsScreen() {
        super(Text.literal("EasyKillMobs"));
    }

    @Override
    protected void init() {

        int centerX = this.width / 2;
        int y = this.height / 2 - 54;

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(
                                "Auto Attack: " +
                                (EasyKillMobsClient.isEnabled() ? "ON" : "OFF")
                        ),
                        button -> {

                            EasyKillMobsClient.setEnabled(
                                    !EasyKillMobsClient.isEnabled()
                            );

                            button.setMessage(
                                    Text.literal(
                                            "Auto Attack: " +
                                            (EasyKillMobsClient.isEnabled() ? "ON" : "OFF")
                                    )
                            );
                        }
                ).dimensions(centerX - 100, y, 200, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(
                                "Cooldown: " +
                                EasyKillMobsClient.getDelayMs() + " ms"
                        ),
                        button -> {

                            int next =
                                    EasyKillMobsClient.getDelayMs() + 100;

                            if (next > 2000) {
                                next = 100;
                            }

                            EasyKillMobsClient.setDelayMs(next);

                            button.setMessage(
                                    Text.literal(
                                            "Cooldown: " +
                                            EasyKillMobsClient.getDelayMs() + " ms"
                                    )
                            );
                        }
                ).dimensions(centerX - 100, y + 26, 200, 20).build()
        );

        addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Close"),
                        button -> close()
                ).dimensions(centerX - 100, y + 58, 200, 20).build()
        );

        if (EasyKillMobsClient.isPermanentlyDisabled()) {

            addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal("Permanently disabled"),
                            button -> {
                            }
                    ).dimensions(centerX - 100, y + 84, 200, 20).build()
            );
        }
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        renderBackground(context);

        context.drawCenteredTextWithShadow(
                textRenderer,
                title,
                width / 2,
                height / 2 - 82,
                0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal(
                        "Cooldown changes in 100 ms steps (100-2000 ms)"
                ),
                width / 2,
                height / 2 + 52,
                0xAAAAAA
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
