import React from 'react';
import type { CustomizationTab } from '../../../types/designer';
import { SIDEBAR_TABS } from '../../../constants/customizationData';

interface LeftSidebarProps {
  activeTab: CustomizationTab;
  onTabChange: (tab: CustomizationTab) => void;
}

export const LeftSidebar: React.FC<LeftSidebarProps> = ({ activeTab, onTabChange }) => {
  return (
    <div className="w-64 border-r border-gray-200 bg-white h-full flex flex-col">
      <div className="p-4 border-b border-gray-100">
        <span className="text-xs font-bold uppercase tracking-wider text-gray-400">Options de Design</span>
      </div>
      <nav className="flex-1 p-2 space-y-1">
        {SIDEBAR_TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={`w-full text-left px-4 py-3 text-sm font-medium rounded-lg transition-colors cursor-pointer ${
              activeTab === tab.id
                ? 'bg-pink-50 text-pink-600 font-semibold'
                : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </nav>
    </div>
  );
};