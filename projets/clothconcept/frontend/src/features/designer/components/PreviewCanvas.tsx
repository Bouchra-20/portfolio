import React from 'react';

interface PreviewCanvasProps {
  currentCombinationUrl: string | null;
}

export const PreviewCanvas: React.FC<PreviewCanvasProps> = ({ currentCombinationUrl }) => {
  return (
    <div className="flex-1 bg-slate-50 flex items-center justify-center p-8">
      <div className="relative w-full max-w-md aspect-[3/4] bg-white rounded-2xl border border-gray-200 shadow-sm flex items-center justify-center p-6">
        
        {currentCombinationUrl ? (
          <img 
            src={currentCombinationUrl} 
            alt="Aperçu du vêtement" 
            className="w-full h-full object-contain mix-blend-multiply transition-all duration-300 animate-fade-in"
          />
        ) : (
          <div className="text-center text-gray-400 text-sm px-6">
            <p>👗 Sélectionnez une encolure et un type de manche pour afficher le croquis technique.</p>
          </div>
        )}

      </div>
    </div>
  );
};