import React from 'react';
import type { StyleOption } from '../../../types/designer';

interface OptionsSelectorProps {
  title: string;
  options: StyleOption[];
  selectedId: string | null;
  onSelect: (id: string) => void;
}

export const OptionsSelector: React.FC<OptionsSelectorProps> = ({ title, options, selectedId, onSelect }) => {
  return (
    <div className="w-full bg-white border-b border-gray-200 p-4">
      <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3">{title}</h3>
      <div className="flex space-x-4 overflow-x-auto pb-2">
        {options.map((option) => {
          const isSelected = selectedId === option.id;
          return (
            <div
              key={option.id}
              onClick={() => onSelect(option.id)}
              className={`flex flex-col items-center p-2 border rounded-lg cursor-pointer min-w-[90px] transition-all ${
                isSelected ? 'border-pink-500 bg-pink-50/30 ring-1 ring-pink-500' : 'border-gray-200 hover:border-gray-400'
              }`}
            >
              <img src={option.thumbnailUrl} alt={option.name} className="w-16 h-16 object-contain mix-blend-multiply mb-1" />
              <span className="text-[11px] text-gray-600 font-medium text-center truncate w-20">{option.name}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
};