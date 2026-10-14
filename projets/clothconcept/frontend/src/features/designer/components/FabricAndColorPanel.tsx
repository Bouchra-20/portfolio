import React from 'react';
import { useFabrics } from '../../../hooks/useFabrics';

interface FabricAndColorPanelProps {
  selectedFabricId: string;
  chosenColor: string;
  onFabricChange: (id: string, nextColor: string) => void;
  onColorChange: (color: string) => void;
}

export const FabricAndColorPanel: React.FC<FabricAndColorPanelProps> = ({
  selectedFabricId,
  chosenColor,
  onFabricChange,
  onColorChange,
}) => {

  const { fabrics, loading, error } = useFabrics();

  const currentFabric = fabrics.find(f => f.id === selectedFabricId) || fabrics[0];

  if (loading) {
    return (
      <div className="w-full bg-white border-b border-gray-200 p-6">
        <div className="flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-pink-500"></div>
          <span className="ml-3 text-gray-600">Chargement des tissus...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full bg-white border-b border-gray-200 p-6 flex flex-col md:flex-row items-start md:items-center gap-8 shadow-xs">
      
      {/* 1. GRILLE DES TEXTILES */}
      <div className="flex-1 w-full">
        <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">1. Choisir la Matière</h3>
        {error && (
          <p className="mb-3 text-xs text-red-500">
            Erreur: {error}. Utilisation des tissus par défaut.
          </p>
        )}
        <div className="flex space-x-4 overflow-x-auto pb-2">
          {fabrics.map((fabric) => {
            const isSelected = selectedFabricId === fabric.id;
            return (
              <div
                key={fabric.id}
                onClick={() => onFabricChange(fabric.id, fabric.defaultColor)}
                className={`flex flex-col items-center p-3 border rounded-xl cursor-pointer min-w-[95px] transition-all ${
                  isSelected ? 'border-pink-500 bg-pink-50/20 ring-2 ring-pink-100' : 'border-gray-200 hover:border-gray-400'
                }`}
              >
                {fabric.type === 'UNI' ? (
                  <div 
                    className="w-12 h-12 rounded-lg border border-gray-300 shadow-inner transition-colors"
                    style={{ backgroundColor: isSelected ? chosenColor : fabric.defaultColor }}
                  />
                ) : (
                  <div 
                    className="w-12 h-12 rounded-lg border border-gray-200 bg-cover bg-center shadow-inner"
                    style={{ backgroundImage: `url(${fabric.textureImgPath})` }}
                  />
                )}
                <span className="text-xs font-semibold text-gray-700 mt-2 text-center truncate w-20">{fabric.name}</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* 2. COLOR PICKER DYNAMIQUE (Uniquement si le textile l'autorise) */}
      {currentFabric.allowCustomColor && (
        <div className="flex flex-col border-l border-gray-200 pl-8 h-full justify-center min-w-[180px] animate-fade-in">
          <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">2. Teinter le tissu</h3>
          <div className="flex items-center space-x-4">
            <div className="relative w-12 h-12 rounded-full border border-gray-300 overflow-hidden shadow-md cursor-pointer">
              <input
                type="color"
                value={chosenColor}
                onChange={(e) => onColorChange(e.target.value)}
                className="absolute inset-0 w-full h-full p-0 border-0 scale-150 cursor-pointer"
              />
            </div>
            <div className="flex flex-col">
              <span className="text-xs font-bold text-gray-800 uppercase font-mono">{chosenColor}</span>
              <span className="text-[10px] text-gray-400">Teinte sur mesure</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
