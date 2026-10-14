import React from 'react';
import type { TargetGender, GarmentTemplate } from '../../../types/designer';
import { GENDER_TABS } from '../../../constants/clothingData';

interface GarmentSelectorProps {
  activeGender: TargetGender;
  templates: GarmentTemplate[];
  selectedTemplateId: string | null;
  onGenderChange: (gender: TargetGender) => void;
  onTemplateSelect: (id: string) => void;
}

export const GarmentSelector: React.FC<GarmentSelectorProps> = ({
  activeGender,
  templates,
  selectedTemplateId,
  onGenderChange,
  onTemplateSelect,
}) => {
  return (
    <div className="w-full max-w-6xl mx-auto px-4 py-8 bg-white min-h-[80vh] flex flex-col items-center">
      
      {/* Title */}
      <h1 className="text-4xl font-extralight tracking-wide text-gray-600 mb-8 font-sans">
        Create new masterpiece
      </h1>

      {/* Tabs Navigation */}
      <div className="flex border-b border-gray-200 mb-12 overflow-x-auto w-full justify-center space-x-8">
        {GENDER_TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => onGenderChange(tab.id)}
            className={`pb-3 text-xs font-bold tracking-widest transition-all duration-200 uppercase whitespace-nowrap border-b-2 cursor-pointer ${
              activeGender === tab.id
                ? 'border-pink-500 text-pink-500 font-extrabold'
                : 'border-transparent text-slate-500 hover:text-slate-800'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Garments Grid Layout */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-x-8 gap-y-12 w-full px-4">
        {templates.map((template) => {
          const isSelected = selectedTemplateId === template.id;
          return (
            <div
              key={template.id}
              onClick={() => onTemplateSelect(template.id)}
              className="group flex flex-col items-center justify-between cursor-pointer transition-transform duration-200 hover:-translate-y-1"
            >
              {/* Technical Drawing Area */}
              <div className={`w-full aspect-[2/3] bg-white rounded-md flex items-center justify-center p-4 border transition-all duration-200 ${
                isSelected 
                  ? 'border-pink-400 ring-2 ring-pink-100 shadow-xs' 
                  : 'border-gray-100 group-hover:border-gray-300'
              }`}>
                <img
                  src={template.vectorUrl}
                  alt={template.name}
                  className={`w-full h-full object-contain mix-blend-multiply transition-opacity ${
                    isSelected ? 'opacity-100' : 'opacity-75 group-hover:opacity-100'
                  }`}
                  loading="lazy"
                />
              </div>

              {/* Label */}
              <span className={`mt-4 text-sm font-light tracking-wide transition-colors ${
                isSelected ? 'text-pink-500 font-normal' : 'text-gray-500 group-hover:text-gray-800'
              }`}>
                {template.name}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
};