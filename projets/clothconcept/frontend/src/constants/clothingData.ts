import type { GarmentTemplate } from '../types/designer';

export const GENDER_TABS = [
  { id: 'WOMENSWEAR', label: 'WOMENSWEAR' },
  { id: 'MENSWEAR', label: 'MENSWEAR' },
  { id: 'GIRLSWEAR', label: 'GIRLSWEAR' },
  { id: 'BOYSWEAR', label: 'BOYSWEAR' },
  { id: 'JUNIOR_GIRLS', label: 'JUNIOR GIRLS' },
  { id: 'JUNIOR_BOYS', label: 'JUNIOR BOYS' },
] as const;

export const GARMENT_TEMPLATES: GarmentTemplate[] = [
  { 
    id: 'tmpl_dress_classic', 
    category: 'Dresses', 
    gender: ['WOMENSWEAR'], 
    name: 'Dresses', 
    vectorUrl: '/patrons/robes.png'
  },
  { 
    id: 'tmpl_top_v', 
    category: 'Tops', 
    gender: ['WOMENSWEAR'], 
    name: 'Tops', 
    vectorUrl: '/patrons/hauts.png'
  },
  { 
    id: 'tmpl_pants_straight', 
    category: 'Pants', 
    gender: ['WOMENSWEAR', 'MENSWEAR'], 
    name: 'Pants', 
    vectorUrl: '/patrons/pantalons.png' 
  },
  {
    id: 'tmpl_skirt_a_line',
    category: 'Skirts',
    gender: ['WOMENSWEAR'],
    name: 'Skirts',
    vectorUrl: '/patrons/jupes.png'
  },
  {
    id: 'tmpl_sweatshirt_hoodie',
    category: 'T-Shirts',
    gender: ['WOMENSWEAR', 'MENSWEAR'],
    name: 'T-Shirts',
    vectorUrl: '/patrons/t-shirts.png'
  },
  {
    id: 'tmpl_jumpsuit_casual',
    category: 'Jumpsuits',
    gender: ['WOMENSWEAR'],
    name: 'Jumpsuits',
    vectorUrl: '/patrons/combis.png'
  }
];