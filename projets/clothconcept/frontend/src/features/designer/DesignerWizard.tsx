import React, { useState, useMemo, useRef } from 'react';
import type { DesignerState } from '../../types/designer';
import { GARMENT_TEMPLATES } from '../../constants/clothingData';
import { STYLE_OPTIONS, COMBINATIONS_IMAGES, FABRIC_OPTIONS } from '../../constants/customizationData';
import { GarmentSelector } from './components/GarmentSelector';
import { LeftSidebar } from './components/LeftSidebar';
import { OptionsSelector } from './components/OptionsSelector';
import { PreviewCanvas } from './components/PreviewCanvas';
import { FabricAndColorPanel } from './components/FabricAndColorPanel';

export const DesignerWizard: React.FC = () => {
  const studioScrollRef = useRef<HTMLDivElement | null>(null);
  const [state, setState] = useState<DesignerState>({
    gender: 'WOMENSWEAR',
    selectedTemplateId: null,
    activeTab: 'Silhouette',
    customization: {
      baseId: 'base_slim', // valeur par défaut
      necklineId: null,
      sleevesId: null,
      waistId: null,
      fabric: {
        id: 'plain cotton',
        chosenColor: '#ffffff'
      }
    },
  });
  
  const [isGenerating, setIsGenerating] = useState(false);
  const [aiResultUrl, setAiResultUrl] = useState<string | null>(null);
  const [showResultPanel, setShowResultPanel] = useState(false);

  const scrollStudio = (direction: 'up' | 'down') => {
    const container = studioScrollRef.current;
    if (!container) return;

    container.scrollBy({
      top: direction === 'down' ? container.clientHeight * 0.75 : -container.clientHeight * 0.75,
      behavior: 'smooth',
    });
  };


  const filteredTemplates = useMemo(() => {
    return GARMENT_TEMPLATES.filter((tmpl) => tmpl.gender.includes(state.gender));
  }, [state.gender]);

    // Calcul de la photo combinée ou de l'image par défaut à afficher au milieu
  const currentCombinationUrl = useMemo(() => {
    const { necklineId, sleevesId, waistId } = state.customization;
    
    const activePieces: string[] = [];
    if (necklineId) activePieces.push(necklineId);
    if (waistId) activePieces.push(waistId);
    if (sleevesId) activePieces.push(sleevesId);

    if (activePieces.length > 0) {
      const combinationKey = activePieces.join('+');
      
      if (COMBINATIONS_IMAGES[combinationKey]) {
        return COMBINATIONS_IMAGES[combinationKey];
      }
      
      const fallbackKey = activePieces.slice(0, 2).join('+');
      if (COMBINATIONS_IMAGES[fallbackKey]) {
        return COMBINATIONS_IMAGES[fallbackKey];
      }
    }

    if (state.selectedTemplateId === 'waist_low') {
      return '/patrons/taille-basse.png'; 
    }
    
    // Par défaut pour les autres types de vêtements si rien n'est coché
    return '/patrons/silhouette-slim.png';

  }, [state.customization, state.selectedTemplateId]);

  if (!state.selectedTemplateId) {
    return (
      <div className="w-full min-h-screen bg-slate-50/50 flex items-center justify-center">
        <GarmentSelector
          activeGender={state.gender}
          templates={filteredTemplates}
          selectedTemplateId={state.selectedTemplateId}
          onGenderChange={(g) => setState(p => ({ ...p, gender: g }))}
          onTemplateSelect={(id) => setState(p => ({ ...p, selectedTemplateId: id }))}
        />
      </div>
    );
  }

  const handleGenerateAI = async () => {
    setIsGenerating(true);
    setShowResultPanel(true);
    setAiResultUrl(null);

    const { necklineId, sleevesId, waistId, fabric } = state.customization;
    
    // 1. Construction du prompt textuel descriptif et précis
    const targetFabric = FABRIC_OPTIONS.find(f => f.id === fabric.id);
    const fabricDescription = targetFabric?.type === 'UNI' 
      ? `premium plain cotton fabric in exact color hex ${fabric.chosenColor}`
      : `high-quality raw ${targetFabric?.name} textile material`;

    const cuts = [];
    if (necklineId) {
      const necklineName = necklineId.replace(/_/g, ' ').replace('neck', 'neckline');
      cuts.push(necklineName);
    }
    if (sleevesId) {
      const sleevesName = sleevesId.replace(/_/g, ' ');
      cuts.push(sleevesName);
    }
    if (waistId) {
      const waistName = waistId.replace(/_/g, ' ');
      cuts.push(waistName);
    }

    const cutsDescription = cuts.length > 0 
      ? `featuring ${cuts.join(', ')}` 
      : 'classic design';

    // Prompt final optimisé pour ControlNet
    const promptText = (
      `Professional fashion photography, full body shot of a female model wearing `
      + `a women's ${state.selectedTemplateId?.replace('tmpl_', '').replace('_', ' ')}, `
      + `${cutsDescription}, `
      + `made of ${fabricDescription}, `
      + `realistic fabric texture, natural folds, studio lighting, `
      + `fashion catalog style, clean white background, `
      + `highly detailed, 8k resolution, photorealistic, masterpiece`
    );
    
    // 2. Préparation du payload standardisé
    const payload = {
      gender: state.gender,
      category: state.selectedTemplateId || "dresses",
      silhouette: state.customization.baseId || "base_slim",
      neckline: necklineId,
      sleeves: sleevesId,
      waist: waistId,
      fabric_type: fabric.id,
      hex_color: fabric.chosenColor,
      prompt: promptText
    };

    try {
      // 3. Appel HTTP vers notre serveur FastAPI Python
      const response = await fetch('http://localhost:8000/api/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({ detail: "Erreur inconnue" }));
        console.error(" Erreur backend:", errorData);
        throw new Error(errorData.detail || `Erreur HTTP ${response.status}`);
      }

      const data = await response.json();
      
      // 4. Injecter l'image reçue directement dans l'espace dédié droit !
      if (data && data.image) {
        setAiResultUrl(data.image);
      } else {
        console.error("Format de réponse inattendu :", data);
        alert("Le serveur a répondu mais aucune image n'a été trouvée.");
      }
    } catch (error) {
      console.error("Erreur de communication avec le serveur IA:", error);
      alert(`Erreur: ${error instanceof Error ? error.message : 'Impossible de joindre le serveur IA'}`);
      setShowResultPanel(false);
    } finally {
      setIsGenerating(false);
    }
  };

  const handleDownload = () => {
  if (!aiResultUrl) return;
  
  // Créer un lien de téléchargement
  const link = document.createElement('a');
  link.href = `data:image/png;base64,${aiResultUrl}`;
  link.download = `creation_${Date.now()}.png`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

    // ÉCRAN DE PERSONNALISATION STYLE (STYLE STUDIO)
  return (
    <div className="w-full h-screen flex flex-col bg-white overflow-hidden">
      {/* Mini Header de contrôle */}
      <div className="h-14 border-b border-gray-200 px-6 flex items-center justify-between bg-white shrink-0">
        <button 
          onClick={() => setState(p => ({ ...p, selectedTemplateId: null }))}
          className="text-sm font-medium text-gray-500 hover:text-gray-800 flex items-center cursor-pointer"
        >
          ← Retour au catalogue
        </button>
        <span className="text-sm font-semibold text-gray-700">Studio de Personnalisation</span>
        <button 
          onClick={handleGenerateAI}
          className="px-5 py-2 bg-gradient-to-r from-pink-500 to-rose-500 text-white text-xs font-bold tracking-widest rounded-lg shadow-md hover:from-pink-600 hover:to-rose-600 transition-all cursor-pointer uppercase"
        >
          Générer avec l'IA ✨
        </button>
      </div>

      {/* Corps du Studio */}
      <div className="flex-1 flex min-h-0 overflow-hidden">
        {/* Barre de gauche */}
        <LeftSidebar 
          activeTab={state.activeTab} 
          onTabChange={(tab) => setState(p => ({ ...p, activeTab: tab }))} 
        />

        {/* Zone centrale & Rendu de droite */}
        <div
          ref={studioScrollRef}
          className="relative flex-1 flex flex-col h-full overflow-y-auto scroll-smooth bg-slate-50"
        >
          
          {/* Grille de sélection des options de style */}
          <OptionsSelector
            title={`Sélectionnez un type de ${state.activeTab}`}
            options={STYLE_OPTIONS[state.activeTab]}
            selectedId={
              state.activeTab === 'Silhouette' ? state.customization.baseId :
              state.activeTab === 'Neckline' ? state.customization.necklineId :
              state.activeTab === 'Sleeves' ? state.customization.sleevesId :
              state.customization.waistId
            }
            onSelect={(id) => setState(p => {
              const key = 
                state.activeTab === 'Silhouette' ? 'baseId' :
                state.activeTab === 'Neckline' ? 'necklineId' :
                state.activeTab === 'Sleeves' ? 'sleevesId' : 'waistId';
              return {
                ...p,
                customization: { ...p.customization, [key]: id }
              };
            })}
          />

          {/* Panel Unique : Choix de la matière */}
          <FabricAndColorPanel
            selectedFabricId={state.customization.fabric.id}
            chosenColor={state.customization.fabric.chosenColor}
            onFabricChange={(id, color) => setState(p => ({
              ...p,
              customization: { ...p.customization, fabric: { id, chosenColor: color } }
            }))}
            onColorChange={(color) => setState(p => ({
              ...p,
              customization: { ...p.customization, fabric: { ...p.customization.fabric, chosenColor: color } }
            }))}
          />

          {/* ESPACE DE RENDU SCINDÉ (Dédié à plat) */}
          <div className="flex flex-col lg:flex-row p-6 gap-6 min-h-[620px]">
            
            {/* Espace 1 : Ton Croquis Technique actuel */}
            <div className="flex-1 flex flex-col">
              <span className="text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-2">
                📐 Croquis Technique de Base
              </span>
              <PreviewCanvas currentCombinationUrl={currentCombinationUrl} />
            </div>

            {/* Espace 2 : Espace dédié au résultat de l'IA */}
            {showResultPanel && (
              <div className="flex-1 flex flex-col animate-fade-in min-w-0">
                <span className="text-[10px] font-bold text-pink-500 uppercase tracking-wider mb-2 flex items-center gap-1.5 shrink-0">
                  <span className="h-1.5 w-1.5 rounded-full bg-pink-500 animate-pulse"></span>
                  📸 Visualisation Photo Réaliste (IA)
                </span>
                
                <div className="flex-1 bg-white rounded-2xl border border-gray-200 shadow-xs flex flex-col p-4 relative overflow-hidden min-h-[600px]">
                  
                  {isGenerating ? (
                    /* État pendant que l'IA tourne */
                    <div className="flex-1 flex items-center justify-center">
                      <div className="text-center space-y-4">
                        <div className="w-14 h-14 border-4 border-t-pink-500 border-r-transparent border-b-pink-500 border-l-transparent rounded-full animate-spin mx-auto"></div>
                        <div className="space-y-1">
                          <p className="text-sm font-semibold text-gray-700">Génération par l'IA en cours...</p>
                          <p className="text-xs text-gray-400 max-w-[240px] mx-auto">Le modèle photo applique tes textures et la coupe demandée.</p>
                        </div>
                      </div>
                    </div>
                  ) : aiResultUrl ? (
                    /* Affichage de la photo finale */
                    <div className="flex-1 flex flex-col min-h-0">
                      {/* Conteneur de l'image avec aspect-ratio portrait */}
                      
                      <div className="flex-1 min-h-0 flex items-center justify-center bg-gradient-to-b from-slate-50 to-white rounded-xl border border-gray-100 overflow-hidden">
                      <div className="w-full h-full aspect-[3/4] max-h-full flex items-center justify-center">
                        <img 
                          src={`data:image/png;base64,${aiResultUrl}`} 
                          alt="Résultat Stable Diffusion" 
                          className="max-w-full max-h-full object-contain"
                        />
                      </div>
                    </div>
                      
                      {/* Boutons d'actions */}
                      <div className="mt-3 flex gap-3 shrink-0">
                        <button className="flex-1 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-bold rounded-lg tracking-wide uppercase cursor-pointer transition-colors" onClick={handleDownload}>
                          📥 Enregistrer le Look
                        </button>
                        </div>
                    </div>
                  ) : (
                    /* État vide - en attente */
                    <div className="flex-1 flex items-center justify-center">
                      <div className="text-center space-y-2">
                        <div className="text-4xl opacity-30">📸</div>
                        <p className="text-xs text-gray-400">L'image générée apparaîtra ici</p>
                      </div>
                    </div>
                  )}

                </div>
              </div>
            )}
          </div>

          <div className="sticky bottom-5 z-20 flex justify-end pr-5 pb-5 pointer-events-none">
            <div className="flex flex-col overflow-hidden rounded-lg border border-gray-200 bg-white shadow-lg pointer-events-auto">
              <button
                type="button"
                onClick={() => scrollStudio('up')}
                className="h-10 w-10 flex items-center justify-center text-gray-600 hover:bg-pink-50 hover:text-pink-600 transition-colors cursor-pointer"
                aria-label="Monter dans l'interface"
                title="Monter"
              >
                ↑
              </button>
              <div className="h-px bg-gray-200" />
              <button
                type="button"
                onClick={() => scrollStudio('down')}
                className="h-10 w-10 flex items-center justify-center text-gray-600 hover:bg-pink-50 hover:text-pink-600 transition-colors cursor-pointer"
                aria-label="Descendre dans l'interface"
                title="Descendre"
              >
                ↓
              </button>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};
