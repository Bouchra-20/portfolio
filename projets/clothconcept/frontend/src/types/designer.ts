export type TargetGender = 'WOMENSWEAR' | 'MENSWEAR' | 'GIRLSWEAR' | 'BOYSWEAR' | 'JUNIOR_GIRLS' | 'JUNIOR_BOYS';
export type GarmentCategory = 'Dresses' | 'Tops' | 'Jumpsuits' | 'Pants' | 'Skirts' | 'Sweatshirts' | 'T-Shirts';

export interface GarmentTemplate {
  id: string;
  category: GarmentCategory;
  gender: TargetGender[];
  name: string;
  vectorUrl: string;
}

// Nouvelle structure pour le style dynamique
export type CustomizationTab = 'Silhouette' | 'Neckline' | 'Sleeves' | 'Waist';

export interface StyleOption {
  id: string;
  name: string;
  thumbnailUrl: string;
  layerUrl: string;
}

export interface DesignerState {
  gender: TargetGender;
  selectedTemplateId: string | null;
  activeTab: CustomizationTab; // Onglet actif à gauche (ex: 'Neckline')
  customization: {
    baseId: string;           // Le buste de base
    necklineId: string | null; // Le col choisi
    sleevesId: string | null;  // La manche choisie
    waistId: string | null;    // La taille choisie
    fabric: {
      id: string;
      chosenColor: string; // La couleur finale (par défaut ou customisée)
    };
  };
}

export interface FabricOption {
  id: string;
  name: string;
  type: 'UNI' | 'TEXTURED';
  textureImgPath: string;
  defaultColor: string;
  allowCustomColor: boolean;
}