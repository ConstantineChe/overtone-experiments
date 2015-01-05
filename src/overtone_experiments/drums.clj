(ns overtone-experiments.drums
  (:require [overtone.studio.fx :as fx])
  (:use [overtone.live]
        [overtone.inst.sampled-piano]
        [overtone.samples.piano :only [index-buffer]]
        [overtone.inst.synth]
        [overtone.inst.drum :as d]))


(def drums-state (atom {:snare {:amp 5 :crackle-amp 20} :hat {:amp 2 :t 0.7 :low 6000 :hi 2000}}))

(swap! drums-state update-in [:snare :amp] (fn [x] 5))
(swap! drums-state update-in [:hat :amp] (fn [x] 1.1))


(fx/fx-reverb)
(kill fx/fx-reverb)

(inst-fx! d/snare fx/fx-reverb)

(fx/fx-echo)

(ctl (fx/fx-echo) :delay-time 1.7 :decay-time 0.5 :max-delay 0.1)

(ctl (fx/fx-echo) :decay-time 2.1)

(kill fx/fx-echo)

(def metro (metronome 70))

(defn metro-fx [m fx instr step]
  (let [beat (m)]
    (at (m beat) (if (zero? (mod beat step)) (inst-fx! instr fx)))
    (at (m (inc beat)) (clear-fx instr))
    (apply-by (m (inc beat)) metro-fx m fx instr step [])))

(metro :bpm 70)

(defn play-beat [m sound step ctl-ref ctl-atom]
  (let [beat (m)]
    (at (m beat) (if (zero? (mod beat step)) (apply sound (ctl-deref ctl-ref ctl-atom))))
    (apply-by (m (inc beat)) play-beat m sound step ctl-ref ctl-atom [])))

(defn play-snare [nome sound ctl-ref]
  (let [beat (nome)]
    (at (nome beat) (apply sound (ctl-deref ctl-ref drums-state)))
    (apply-by (nome (inc beat)) play-snare nome sound ctl-ref [])))

(defn ctl-deref [ctl-ref ctl-atom]
  (let [ctl-map (ctl-ref @ctl-atom)]
    (apply concat (for [key (keys ctl-map)]
                    (list key (get ctl-map key))))))

(ctl-deref :hat drums-state)

(play-beat metro d/snare 1 :snare drums-state)
(play-beat metro d/open-hat 2 :hat drums-state)
(metro-fx metro fx/fx-reverb d/snare 3)

(ctl d/snare :crackle-amp 20 :amp 5)

(d/snare)
(metro)

(stop)

(inst-fx! d/snare fx/fx-echo)
(clear-fx d/snare)

(print (ctl d/snare :crackle-amp 20.0 :amp 5))
(def my-hat (d/open-hat :amp 2 :t 0.7))
(def my-snare (d/snare :crackle-amp 20.0 :amp 5))
(d/closed-hat)
