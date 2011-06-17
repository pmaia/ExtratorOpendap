package br.edu.ufcg.lsd.seghidro.extratoropendap.interpolador;

import java.io.IOException;

import ucar.ma2.Array;
import ucar.ma2.IndexIterator;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dataset.VariableEnhanced;
import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.grid.GeoGrid;
import ucar.nc2.dt.grid.GridDataset;
import ucar.unidata.geoloc.LatLonPointImpl;
import br.edu.ufcg.lsd.seghidro.extratoropendap.exceptions.ExtratorOpendapException;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.Coordenadas;
import br.edu.ufcg.lsd.seghidro.extratoropendap.model.DataSet;
import br.edu.ufcg.lsd.seghidro.extratoropendap.util.NetcdfAPIUtil;

/**
 * 
 * Interpolador Bilinear específico para os dados do Tipo NetCDF acessados por um arquivo local.
 * 
 * @author edigley
 *
 */
public class InterpoladorBilinearNetCDF implements InterpoladorIF {

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.seghidro.mudancasClimaticas.extrator.interpolador.InterpoladorIF#interpola(br.edu.ufcg.lsd.seghidro.mudancasClimaticas.extrator.DataSet, br.edu.ufcg.lsd.seghidro.mudancasClimaticas.extrator.Coordenadas)
	 */
	public Double interpola(DataSet dataSet, String variavel, Coordenadas coordenadas) throws ExtratorOpendapException {
		//TODO Verificar qual o comportamento para o caso em que o valor esteja fielmente representado no arquivo netCDF.
		try {
			int time = dataSet.getIndexFromTime(coordenadas.getTime());
			
			if (time == -1) {
				throw new ExtratorOpendapException("DataSet de origem não possui dados para a data especificada.");
			}
			
			GridDataset gridDS = dataSet.getGridDs();
			GeoGrid grid = dataSet.getFirstGrid();
			VariableEnhanced variable = grid.getVariable();
			GridCoordSystem coordinateSystem = grid.getCoordinateSystem();
			CoordinateAxis longitudeVariable = coordinateSystem.getXHorizAxis();
			CoordinateAxis latitudeVariable = coordinateSystem.getYHorizAxis();
			Variable longitudeBoundsVariable = gridDS.getNetcdfDataset().findVariable(longitudeVariable.getName() + "_bounds");
			Variable latitudeBoundsVariable = gridDS.getNetcdfDataset().findVariable(latitudeVariable.getName() + "_bounds");
			
			if (latitudeBoundsVariable == null) {
				throw new ExtratorOpendapException("Arquivo de origem não possui a variável "+longitudeVariable.getName() + "_bounds"+" necessária para a interpolação.");
			}
			
			if (longitudeBoundsVariable == null) {
				throw new ExtratorOpendapException("Arquivo de origem não possui a variável "+latitudeVariable.getName() + "_bounds"+" necessária para a interpolação.");
			}
						
			int[] xy = coordinateSystem.findXYindexFromCoord(coordenadas.getLongitude(), coordenadas.getLatitude(), null);
			int longitude = xy[0];
			int latitude = xy[1];

			int x1 = longitude;
			int y1 = latitude;
			Array long_bounds = longitudeBoundsVariable.read(new int[] { x1, 0 }, new int[] { 1, 2 });
			IndexIterator longBoundsIt = long_bounds.getIndexIterator();
			double long_l1 = (Float) longBoundsIt.next();
			double long_l2 = (Float) longBoundsIt.next();
			double long_l = long_l2 - long_l1;

			Array lat_bounds = latitudeBoundsVariable.read(new int[] { y1, 0 }, new int[] { 1, 2 });
			IndexIterator latBoundsIt = lat_bounds.getIndexIterator();
			double lat_l1 = (Float) latBoundsIt.next();
			double lat_l2 = (Float) latBoundsIt.next();
			double lat_l = lat_l2 - lat_l1;

			double p1x = LatLonPointImpl.lonNormal(NetcdfAPIUtil.readSingleValueAsFloat(longitudeVariable, new int[] { x1 }));
			double p1y = NetcdfAPIUtil.readSingleValueAsFloat(latitudeVariable, new int[] { y1 });

			if ((coordenadas.getLongitude() - p1x) < 0) {
				x1 = x1 - 1;
				p1x = LatLonPointImpl.lonNormal(NetcdfAPIUtil.readSingleValueAsFloat(longitudeVariable, new int[] { x1 }));
			}

			if ((coordenadas.getLatitude() - p1y) < 0) {
				y1 = y1 - 1;
				p1y = NetcdfAPIUtil.readSingleValueAsFloat(latitudeVariable, new int[] { y1 });
			}

			int x2 = x1 + 1;
			int y2 = y1 + 1;

			double xw1 = (coordenadas.getLongitude() - p1x) / long_l;
			double xw2 = 1d - xw1;

			double yw1 = (coordenadas.getLatitude() - p1y) / lat_l;
			double yw2 = 1d - yw1;

			int[] unicoElemento = new int[] { 1, 1, 1 };
			double f11 = NetcdfAPIUtil.readAsFloat(variable, new int[] { time, y1, x1 }, unicoElemento);
			double f12 = NetcdfAPIUtil.readAsFloat(variable, new int[] { time, y2, x1 }, unicoElemento);
			double f21 = NetcdfAPIUtil.readAsFloat(variable, new int[] { time, y1, x2 }, unicoElemento);
			double f22 = NetcdfAPIUtil.readAsFloat(variable, new int[] { time, y2, x2 }, unicoElemento);

			return f11 * xw2 * yw2 + f21 * xw1 * yw2 + f12 * xw2 * yw1 + f22 * xw1 * yw1;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidRangeException e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
