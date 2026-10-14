import { useState, useEffect } from 'react';
import type { FabricOption } from '../types/designer';
import { FABRIC_OPTIONS } from '../constants/customizationData';

const API_BASE_URL = 'http://localhost:8000';

export const useFabrics = () => {
  const [fabrics, setFabrics] = useState<FabricOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFabrics = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/fabrics`);
        if (!response.ok) {
          throw new Error('Impossible de charger les tissus');
        }
        
        const data = await response.json();
        
        // Transformer les données backend en FabricOption
        const fabricOptions: FabricOption[] = data.fabrics.map((f: any) => ({
          id: f.id,
          name: f.name,
          type: f.type as 'UNI' | 'TEXTURED',
          textureImgPath: f.imageUrl || f.texture_img_path || '',
          defaultColor: f.default_color,
          allowCustomColor: f.allow_custom_color,
        }));
        
        setFabrics(fabricOptions);
        setError(null);
      } catch (err) {
        console.error('Erreur chargement tissus:', err);
        setError(err instanceof Error ? err.message : 'Erreur inconnue');
        
        // Fallback sur les données locales en cas d'erreur
        setFabrics(FABRIC_OPTIONS);
      } finally {
        setLoading(false);
      }
    };

    fetchFabrics();
  }, []);

  return { fabrics, loading, error };
};
