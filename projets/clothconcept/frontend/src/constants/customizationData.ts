import type { CustomizationTab, StyleOption } from '../types/designer';
import type { FabricOption } from '../types/designer';

export const SIDEBAR_TABS: { id: CustomizationTab; label: string }[] = [
  { id: 'Neckline', label: 'Encolure / Col' },
  { id: 'Sleeves', label: 'Manches' },
  { id: 'Silhouette', label: 'Silhouette' },
  { id: 'Waist', label: 'Taille' },
];

export const STYLE_OPTIONS: Record<Extract<CustomizationTab, 'Neckline' | 'Sleeves' | 'Silhouette' | 'Waist'>, StyleOption[]> = {
  Neckline: [
    { id: 'v_neck', name: 'Col en V', thumbnailUrl: '/patrons/col-v.png', layerUrl: '' },
    { id: 'round_neck', name: 'Col Rond', thumbnailUrl: '/patrons/col-rond.png', layerUrl: '' },
    { id: 'collar_neck', name: 'Col Collar', thumbnailUrl: '/patrons/col-collar.png', layerUrl: '' },
  ],
  Sleeves: [
    { id: 'short_sleeves', name: 'Manches Courtes', thumbnailUrl: '/patrons/manche-courte.png', layerUrl: '' },
    { id: 'long_sleeves', name: 'Manches Longues', thumbnailUrl: '/patrons/manche-longue.png', layerUrl: '' },
  ],
  Silhouette: [
    { id: 'base_slim', name: 'Silhouette Slim', thumbnailUrl: '/patrons/silhouette-slim.png', layerUrl: '' },
    { id: 'base_fitted', name: 'Silhouette Ajustée', thumbnailUrl: '/patrons/silhouette-ajustee.png', layerUrl: '' },
  ],
  Waist: [
    { id: 'waist_low', name: 'Taille Basse', thumbnailUrl: '/patrons/taille-basse.png', layerUrl: '' },
    { id: 'waist_high', name: 'Taille Haute', thumbnailUrl: '/patrons/taille-haute.png', layerUrl: '' },
  ],
};

// C'est cette table qui fait l'association dynamique pour l'image du milieu
export const COMBINATIONS_IMAGES: Record<string, string> = {
    'v_neck+waist_low': '/patrons/col-v-taille-basse.png',
    'v_neck+waist_high': '/patrons/col-v-taille-haute.png',
    'collar_neck+waist_low': '/patrons/col-collar-taille-basse.png',
    'collar_neck+waist_high': '/patrons/col-collar-taille-haute.png',
    'round_neck+waist_low': '/patrons/col-rond-taille-basse.png',
    'round_neck+waist_high': '/patrons/col-rond-taille-haute.png',
    'round_neck+waist_low+short_sleeves': '/patrons/taille-basse-court-manches.png',
    'round_neck+waist_low+long_sleeves': '/patrons/taille-basse-long-manches.png',
    'round_neck+waist_high+short_sleeves': '/patrons/taille-haute-court-manches.png',
    'round_neck+waist_high+long_sleeves': '/patrons/taille-haute-long-manches.png',
  'v_neck+short_sleeves': '/patrons/top-v-court.png',
  'v_neck+long_sleeves': '/patrons/top-v-long.png',
  'v_neck+waist_low+short_sleeves': '/patrons/col-v-taille-basse-court-manches.png',
  'v_neck+waist_low+long_sleeves': '/patrons/col-v-taille-basse-long-manches.png',
  'v_neck+waist_high+short_sleeves': '/patrons/col-v-taille-haute-court-manches.png',
  'v_neck+waist_high+long_sleeves': '/patrons/col-v-taille-haute-long-manches.png',
  'collar_neck+short_sleeves': '/patrons/top-collar-court.png',
  'collar_neck+long_sleeves': '/patrons/top-collar-long.png',
  'collar_neck+waist_low+short_sleeves': '/patrons/col-collar-taille-basse-court-manches.png',
  'collar_neck+waist_low+long_sleeves': '/patrons/col-collar-taille-basse-long-manches.png',
  'collar_neck+waist_high+short_sleeves': '/patrons/col-collar-taille-haute-court-manches.png',
  'collar_neck+waist_high+long_sleeves': '/patrons/col-collar-taille-haute-long-manches.png',
  'round_neck+short_sleeves': '/patrons/top-rond-court.png',
  'round_neck+long_sleeves': '/patrons/top-rond-long.png',
  'waist_low+short_sleeves': '/patrons/taille-basse-court-manches.png',
  'waist_low+long_sleeves': '/patrons/taille-basse-long-manches.png',
  'waist_high+short_sleeves': '/patrons/taille-haute-court-manches.png',
  'waist_high+long_sleeves': '/patrons/taille-haute-long-manches.png',
};

export const FABRIC_OPTIONS: FabricOption[] = [
  {
    id: 'cotton_uni',
    name: 'Coton Uni',
    type: 'UNI',
    textureImgPath: '/textures/cotton_uni_white.jpg',
    defaultColor: '#ffffff',
    allowCustomColor: true,
  },
  {
    id: 'denim',
    name: 'Denim',
    type: 'TEXTURED',
    textureImgPath: '/textures/denim.jpg',
    defaultColor: '#2b4360',
    allowCustomColor: false,
  },
  {
    id: 'rough_linen',
    name: 'Rough Linen',
    type: 'TEXTURED',
    textureImgPath: '/textures/rough_linen.jpg',
    defaultColor: '#b3c8e4',
    allowCustomColor: false,
  },
  {
    id: 'leather_brown',
    name: 'Leather',
    type: 'TEXTURED',
    textureImgPath: '/textures/leather_brown.jpg',
    defaultColor: '#611e1e',
    allowCustomColor: false,
  }
];