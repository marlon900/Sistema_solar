package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.control.AbstractControl;

/**
 * Programa que recrea un pequeño sistema solar.
 * @author marlon
 */
public class SistemaSolar extends SimpleApplication {

    private static final int NUM_PLANETAS = 5;
    private static final float[] ORBI_RAD = {10f, 15f, 20f, 25f, 30f};
    private static final float[] ORBI_VEL = {0.5f, 0.4f, 0.3f, 0.2f, 0.1f};
    private static final float[] ROT_VEL = {1.5f, 2f, 2.5f, 3f, 3.5f};

    public static void main(String[] args) {
        SistemaSolar app = new SistemaSolar();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);

        // Crear el sol
        Geometry sol = crearPlaneta("Sol", 3f, ColorRGBA.Yellow);
        rootNode.attachChild(sol);

        // Crear planetas
        for (int i = 0; i < NUM_PLANETAS; i++) {
            Geometry planeta = crearPlaneta("Planeta " + i, 0.5f, ColorRGBA.randomColor());
            float orbitaRadio = ORBI_RAD[i];
            float orbitaVelocidad = ORBI_VEL[i];
            float rotationSpeed = ROT_VEL[i];
            rootNode.attachChild(planeta);
            planeta.setLocalTranslation(orbitaRadio, 0, 0);
            OrbitControl orbitaControl = new OrbitControl(orbitaRadio, orbitaVelocidad);
            planeta.addControl(orbitaControl);
            RotationControl rotationControl = new RotationControl(rotationSpeed);
            planeta.addControl(rotationControl);

            // Agregar una luna al tercer planeta
            if (i == 2) {
                // Crear un nodo para el tercer planeta
                Node tierraPlaneta = new Node("TercerPlanetaNode");
                rootNode.attachChild(tierraPlaneta);

                // Adjuntar el tercer planeta al nodo del tercer planeta
                tierraPlaneta.attachChild(planeta);

                // Crear un nodo para la luna
                Node lunaNodo = new Node("LunaNodo");
                tierraPlaneta.attachChild(lunaNodo);

                // Crear la luna
                Geometry luna = crearPlaneta("Luna", 0.7f, ColorRGBA.White);
                luna.setLocalTranslation(5f, 0, 0); // Ajustar la posición de la luna
                lunaNodo.attachChild(luna);

                // Agregar control de órbita a la luna
                LunaOrbitaControl lunaOrbitaControl = new LunaOrbitaControl(tierraPlaneta, 2f, 0.8f); // Pasar tierraPlaneta como referencia
                luna.addControl(lunaOrbitaControl);
            }
        }
    }

    private Geometry crearPlaneta(String nombre, float radio, ColorRGBA color) {
        Box box = new Box(radio, radio, radio);
        Geometry planeta = new Geometry(nombre, box);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        planeta.setMaterial(material);
        return planeta;
    }

    private class OrbitControl extends AbstractControl {
        private float radius;
        private float speed;
        private float angle = 0;

        public OrbitControl(float radius, float speed) {
            this.radius = radius;
            this.speed = speed;
        }

        @Override
        protected void controlUpdate(float tpf) {
            angle += speed * tpf;
            float x = FastMath.cos(angle) * radius;
            float z = FastMath.sin(angle) * radius;
            spatial.setLocalTranslation(x, 0, z);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
    }

    private class RotationControl extends AbstractControl {
        private float velocidad;

        public RotationControl(float velocidad) {
            this.velocidad = velocidad;
        }

        @Override
        protected void controlUpdate(float tpf) {
            spatial.rotate(0, velocidad * tpf, 0);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
    }

    private class LunaOrbitaControl extends AbstractControl {
        private Node tercerPlaneta;
        private float radio;
        private float velocidad;
        private float angulo = 0;

        public LunaOrbitaControl(Node tercerPlaneta, float radius, float speed) {
            this.tercerPlaneta = tercerPlaneta;
            this.radio = radius;
            this.velocidad = speed;
        }

        @Override
        protected void controlUpdate(float tpf) {
            angulo += velocidad * tpf;
            float x = tercerPlaneta.getWorldTranslation().x + FastMath.cos(angulo) * radio;
            float z = tercerPlaneta.getWorldTranslation().z + FastMath.sin(angulo) * radio;
            spatial.setLocalTranslation(x, 0, z);
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
    }
}
