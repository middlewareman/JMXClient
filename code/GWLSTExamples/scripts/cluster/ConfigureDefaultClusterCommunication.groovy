/* $Id$ */
/** 
 * Reset default cluster communication. 
 * @author Andreas Nyberg
 */
editSave { domain ->
	for (cluster in domain.Clusters) {
		cluster.unSet 'ClusterAddress'
		cluster.unSet 'ClusterBroadcastChannel'
		cluster.unSet 'ClusterMessagingMode'
	}
}