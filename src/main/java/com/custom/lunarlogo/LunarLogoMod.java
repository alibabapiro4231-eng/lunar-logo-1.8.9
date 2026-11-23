package com.custom.lunarlogo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;

@Mod(modid = LunarLogoMod.MODID, version = LunarLogoMod.VERSION, clientSideOnly = true)
public class LunarLogoMod {
    public static final String MODID = "lunarlogo";
    public static final String VERSION = "1.0";
    private static ResourceLocation lunarIconLocation;

    private static final String[] LUNAR_PIXEL_ART = {
        "......XXXX......",
        "....XXXXXXXX....",
        "...XXXX..XXXX...",
        "..XXXX........X.",
        ".XXXX........XXX",
        ".XXXX.........X.",
        "XXXX............",
        "XXXX........X...",
        "XXXX.......XXX..",
        "XXXX........X...",
        ".XXXX...........",
        ".XXXX.........X.",
        "..XXXX.......XXX",
        "...XXXX..XXXX.X.",
        "....XXXXXXXX....",
        "......XXXX......"
    };

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private ResourceLocation getLunarTexture() {
        if (lunarIconLocation == null) {
            BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < 16; y++) {
                for (int x = 0; x < 16; x++) {
                    if (LUNAR_PIXEL_ART[y].charAt(x) == 'X') {
                        img.setRGB(x, y, 0xFFFFFFFF);
                    } else {
                        img.setRGB(x, y, 0x00000000);
                    }
                }
            }
            DynamicTexture dynTex = new DynamicTexture(img);
            lunarIconLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("lunar_client_logo", dynTex);
        }
        return lunarIconLocation;
    }

    @SubscribeEvent
    public void onRenderName(RenderLivingEvent.Specials.Post event) {
        if (!(event.entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entity;

        if (player.isSneaking() || player.isInvisible()) return;

        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = mc.getRenderManager();
        FontRenderer fontRenderer = mc.fontRendererObj;

        String displayName = player.getDisplayName().getFormattedText();
        int textWidth = fontRenderer.getStringWidth(displayName);

        float scale = 0.02666667F;
        double x = event.x;
        double y = event.y + player.height + 0.5D;
        double z = event.z;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        mc.getTextureManager().bindTexture(getLunarTexture());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int iconSize = 10;
        int iconX = -(textWidth / 2) - iconSize - 3;
        int iconY = -1;

        drawTexturedRect(iconX, iconY, iconSize, iconSize);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawTexturedRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
    }
}
